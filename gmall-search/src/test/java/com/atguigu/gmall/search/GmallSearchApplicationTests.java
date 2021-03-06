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
            //???spu?????????????????????goods?????????????????????
            //????????????spu
            ResponseVo<List<SpuEntity>> spuListResponseVo= pmsClient.querySpuByPage(pageParamVo);
            //???????????????????????????
            List<SpuEntity> spuEntities = spuListResponseVo.getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                continue;
            }

            //1?????????????????????spuEntity
            if(!CollectionUtils.isEmpty(spuEntities)){
                spuEntities.forEach(spuEntity -> {
                    //2?????????spu??????sku??????
                    ResponseVo<List<SkuEntity>> skuListResponseVo = this.pmsClient.querySkuBySquId(spuEntity.getId());
                    List<SkuEntity> skuEntities = skuListResponseVo.getData();
                    logger.info("-skuEntities=-"+skuEntities);

                    if(!CollectionUtils.isEmpty(skuEntities)){
                        //3???sku???????????????goods
                        List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
                            Goods goods = new Goods();
                            goods.setSkuId(skuEntity.getId());
                            goods.setTitle(skuEntity.getTitle());
                            goods.setPrice(skuEntity.getPrice().doubleValue());
                            goods.setDefaultImage(skuEntity.getDefaultImage());


                            //????????????????????????
                            ResponseVo<BrandEntity> brandListResponseVo = this.pmsClient.queryBrandById(skuEntity.getBrandId());
                            BrandEntity brandEntity = brandListResponseVo.getData();
                            if(brandEntity!=null){
                                goods.setBrandId(skuEntity.getBrandId());
                                goods.setBrandName(brandEntity.getName());
                                goods.setLogo(brandEntity.getLogo());
                            }

                            //????????????????????????
                            ResponseVo<CategoryEntity> categoryEntityResponseVo = this.pmsClient.queryCategoryById(skuEntity.getCategoryId());
                            CategoryEntity categoryEntity = categoryEntityResponseVo.getData();
                            if(categoryEntity!=null){
//                                goods.setCategoryId(categoryEntity.getParentId());  2021???5???25???16:26:45  ????????????
                                goods.setCategoryId(skuEntity.getCategoryId());
                                goods.setCategoryName(categoryEntity.getName());
                            }

                            goods.setCreateTime(spuEntity.getCreateTime());

                            // ??????sku?????????????????????????????????
                            ResponseVo<List<WareSkuEntity>> wareSkuListResponseVo = this.wmsClient.queryWareSkusBySkuId(skuEntity.getId());
                            List<WareSkuEntity> wareSkuEntities = wareSkuListResponseVo.getData();
                            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                                //reduce????????????optionl ???????????????get??????
                                goods.setSales(wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce((a,b)->a+b).get());
                                //anymatch  ???1???????????????true ????????????????????????
                                goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()>0));
                            }else{
//                                goods.setStore(false);  ???????????????????????????????????????
                            }


                            //??????????????????  ??????????????? ????????????/???????????? ?????? ???????????????null
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
