package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.bean.SearchParmVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    public String search(SearchParmVo searchParmVo) {
        //构建查询条件
        SearchRequest searchRequest = new SearchRequest(new String[]{"goods"},this.bulidDsl(searchParmVo));
        //执行查询
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果集
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.toString();
    }


    public SearchSourceBuilder bulidDsl(SearchParmVo searchParmVo) {
        //构建查询条件
        SearchSourceBuilder searchSourceBuilder =new SearchSourceBuilder();
        String keyword = searchParmVo.getKeyword();
        if(StringUtils.isBlank(keyword)){
            // 打广告，TODO
            return null;
        }
        //search?keyword=Redmi Mate&brandId=1,2,3&cid=225&props=6:8-256G&sort=1:desc&priceFrom=0&priceTo=20000&pageNum=0
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 1.1. 匹配查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.OR));

        //品牌过滤
        List<Long> brandIds = searchParmVo.getBrandId();
        if(!CollectionUtils.isEmpty(brandIds)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParmVo.getBrandId()));

        }
        //1.2.2 分类过滤
        String cid = searchParmVo.getCid();
        if(cid!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId",cid));

        }
        //1.2.3 价格过滤
        Double priceTo = searchParmVo.getPriceTo();
        Double priceFrom = searchParmVo.getPriceFrom();
        if(priceFrom!=null||priceTo!=null){
            RangeQueryBuilder rangeQuery =QueryBuilders.rangeQuery("price");
            if(priceFrom!=null){
                rangeQuery.gte(priceFrom);
            }
            if(priceTo!=null){
                rangeQuery.lte(priceTo);
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        //1.2.4 是否有货的过滤
        Boolean store = searchParmVo.getStore();
        if(store!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("store",false));
        }

        //1.2.5 规格参数的过滤
        List<String> props = searchParmVo.getProps();
        if(!CollectionUtils.isEmpty(props)){
            props.forEach(prop ->{
                String[] attrs = StringUtils.split(prop,":");
                //要判断地址栏是否合法  因为有可能别人随便写的 2021年5月26日10:18:58
                if(attrs!=null&&attrs.length==2){
                    String attrId = attrs[0];
                    String attrValueString = attrs[1];
                    String[] attrValues = StringUtils.split(attrValueString,"-");
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.termQuery("searchAttrs.attrId",attrId));
                    boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue",attrValues));
                    //我们的过滤不需要得分 所以得分模式是Null
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs",boolQuery, ScoreMode.None));

                }

            });
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // 2. 构建排序 0-默认，得分降序；1-按价格升序；2-按价格降序；3-按创建时间降序；4-按销量降序
        String sort = searchParmVo.getSort();

        if(StringUtils.isNoneBlank(sort)){
            String[] sorts = StringUtils.split(sort, "");
            if(sorts!=null&&sorts.length==2){
                String field = "";
                switch (sorts[0]){
                    case "1":field = "price";
                    break;
                    case "2":field = "createTime";
                        break;
                    case "3":field = "sales";
                        break;
                }
                //后面2个不是字符串！是枚举！  2021年5月26日10:53:40
                searchSourceBuilder.sort(field,StringUtils.equals(sorts[1],"desc")? SortOrder.DESC:SortOrder.ASC);
            }
        }



        //3、构建分页
        Integer pageNum = searchParmVo.getPageNum();
        Integer pageSize = searchParmVo.getPageSize();
        searchSourceBuilder.from((pageNum -1)*pageSize);
        searchSourceBuilder.size(pageSize);

        //4、构建高亮  是.filed而不是构造函数
        searchSourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<font style='color:red'>").postTags("</font>"));

        //5、构建聚合

        //5.1  不然就是在品牌下面还要聚合了  -- 排版跟老师来
        searchSourceBuilder.aggregation(AggregationBuilders.terms("brandIdAggs").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAggs").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAggs").field("logo")));
        //根据名称来判断  代码写在哪个点后面！


        //5,2 5.2. 构建分类聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("categoryAggs").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAggs").field("categoryName")));


        //5.3 构建规格参数的嵌套聚合
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","searchAttrs")
        .subAggregation(AggregationBuilders.terms("attrIdAggs").field("searchAttrs.attrId")
        .subAggregation(AggregationBuilders.terms("attrNameAggs").field("searchAttrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAggs").field("searchAttrs.attrValue")) ));


//        searchSourceBuilder.aggregation(null);
        //6、构建结果集过滤
        searchSourceBuilder.fetchSource(new String[]{"skuId","title","price","defaultImage"},null);

        System.out.println(searchSourceBuilder.toString());
        return searchSourceBuilder;
    }
}
