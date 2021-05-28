package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.bean.Goods;
import com.atguigu.gmall.search.bean.SearchAttrValue;
import com.atguigu.gmall.search.bean.repository.GoodsRepository;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    public static void main(String[] args) {
        Goods goods = new Goods();
        String a = UUID.randomUUID().toString();
        System.out.println("1");
        System.out.println("2");
        goods.setCategoryName("11");
    }

    private Logger logger = LoggerFactory.getLogger(GmallSearchApplicationTests.class);

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    void testDebug() {
        Goods goods = new Goods();
        String a = UUID.randomUUID().toString();
        System.out.println("1");
        System.out.println("2");
        goods.setCategoryName("11");
       // this.elasticsearchRestTemplate.putMapping(Goods.class);
    }


    @Test
    void contextLoads() {
        this.elasticsearchRestTemplate.createIndex(Goods.class);
        this.elasticsearchRestTemplate.putMapping(Goods.class);
    }


    @Test
    void importData(){
        Integer pageNum = 0;
        Integer pageSize = 100;
        ResponseVo<List<WareSkuEntity>> wareSkuListResponseVo2 = this.wmsClient.queryWareSkusBySkuId(1l);

        do{
            PageParamVo pageParamVo = new PageParamVo();
            pageParamVo.setPageNum(pageNum);
            pageParamVo.setPageSize(pageSize);
            //把spu相关数据转化为goods数据导入索引库
            //分批查询spu
            ResponseVo<List<SpuEntity>> spuListResponseVo= pmsClient.querySpuByPage(pageParamVo);
            //获取当前页的记录数
            List<SpuEntity> spuEntities = spuListResponseVo.getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                continue;
            }

            //1、遍历当前页的spuEntity
            if(!CollectionUtils.isEmpty(spuEntities)){
                spuEntities.forEach(spuEntity -> {
                    //2、查询spu下的sku集合
                    ResponseVo<List<SkuEntity>> skuListResponseVo = this.pmsClient.querySkuBySquId(spuEntity.getId());
                    List<SkuEntity> skuEntities = skuListResponseVo.getData();
                    logger.info("-skuEntities=-"+skuEntities);

                    if(!CollectionUtils.isEmpty(skuEntities)){
                        //3、sku集合转换为goods
                        List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
                            Goods goods = new Goods();
                            goods.setSkuId(skuEntity.getId());
                            goods.setTitle(skuEntity.getTitle());
                            goods.setPrice(skuEntity.getPrice().doubleValue());
                            goods.setDefaultImage(skuEntity.getDefaultImage());


                            //查询品牌相关数据
                            ResponseVo<BrandEntity> brandListResponseVo = this.pmsClient.queryBrandById(skuEntity.getBrandId());
                            BrandEntity brandEntity = brandListResponseVo.getData();
                            if(brandEntity!=null){
                                goods.setBrandId(skuEntity.getBrandId());
                                goods.setBrandName(brandEntity.getName());
                                goods.setLogo(brandEntity.getLogo());
                            }

                            //查询分类相关数据
                            ResponseVo<CategoryEntity> categoryEntityResponseVo = this.pmsClient.queryCategoryById(skuEntity.getCategoryId());
                            CategoryEntity categoryEntity = categoryEntityResponseVo.getData();
                            if(categoryEntity!=null){
//                                goods.setCategoryId(categoryEntity.getParentId());  2021年5月25日16:26:45  写错了！
                                goods.setCategoryId(skuEntity.getCategoryId());
                                goods.setCategoryName(categoryEntity.getName());
                            }

                            goods.setCreateTime(spuEntity.getCreateTime());

                            // 查询sku对应商品库存的相关信息
                            ResponseVo<List<WareSkuEntity>> wareSkuListResponseVo = this.wmsClient.queryWareSkusBySkuId(skuEntity.getId());
                            List<WareSkuEntity> wareSkuEntities = wareSkuListResponseVo.getData();
                            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                                //reduce这边返回optionl 所以还需要get下。
                                goods.setSales(wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce((a,b)->a+b).get());
                                //anymatch  有1个满足则为true 这边不要加大括号
                                goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()>0));
                            }else{
//                                goods.setStore(false);  改为在实体类那边设置默认值
                            }


                            //查询规格参数  第二个参数 普通属性/销售属性 都要 所以设置为null
                            ResponseVo<List<AttrEntity>> attrEntityResponseVo = this.pmsClient.queryAttrByCid(skuEntity.getCategoryId(), null, 1);
                            List<AttrEntity> attrEntities = attrEntityResponseVo.getData();

                            if(!CollectionUtils.isEmpty(attrEntities)){
                                List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
                                List<SearchAttrValue> searchAttrValues = new ArrayList<>();

                                ResponseVo<List<SkuAttrValueEntity>> skuAttrValuesResponseVo = this.pmsClient.querySkuSearchAttrValue(skuEntity.getId(), attrIds);
                                ResponseVo<List<SpuAttrValueEntity>> spuAttrValuesResponseVo = this.pmsClient.querySpuSearchAttrValue(spuEntity.getId(), attrIds);
                                List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValuesResponseVo.getData();
                                List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValuesResponseVo.getData();
                                if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                                    searchAttrValues.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                                        SearchAttrValue searchAttrValue = new SearchAttrValue();
                                        BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValue);
                                        return searchAttrValue;
                                    }).collect(Collectors.toList()));
                                }
                                if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                                    searchAttrValues.addAll(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                        SearchAttrValue searchAttrValue = new SearchAttrValue();
                                        BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValue);
                                        return searchAttrValue;
                                    }).collect(Collectors.toList()));
                                }

                                goods.setSearchAttrs(searchAttrValues);

                            }



                            return goods;
                        }).collect(Collectors.toList());
                        logger.info("-goodsList=-"+goodsList);
                        if(!CollectionUtils.isEmpty(goodsList)){
                            this.goodsRepository.saveAll(goodsList);

                        }
                    }


                });
            }
            pageNum++;
            pageSize = spuEntities.size();
        }while(pageSize==100);
    }
}
