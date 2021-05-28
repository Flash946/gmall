package com.atguigu.guli.item.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.guli.item.feign.GmallPmsClient;
import com.atguigu.guli.item.feign.GmallSmsClient;
import com.atguigu.guli.item.feign.GmallWmsClient;
import com.atguigu.guli.item.vo.AttrValueVo;
import com.atguigu.guli.item.vo.ItemVo;
import com.latguigu.gmall.sms.vo.ItemSaleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;
    public ItemVo load(Long skuId) {
        ItemVo itemVo = new ItemVo();

        //根据skuId查询sku的信息
        ResponseVo<SkuEntity> skuEntityResponseVo = gmallPmsClient.querySkuById(skuId);
        SkuEntity skuEntity = skuEntityResponseVo.getData();
        if(skuEntity==null){
            return null;
        }
        itemVo.setSkuId(skuEntity.getId());
        itemVo.setTitle(skuEntity.getTitle());
        itemVo.setSubTitle(skuEntity.getSubtitle());
        itemVo.setPrice(skuEntity.getPrice());
        itemVo.setWeight(skuEntity.getWeight());
        itemVo.setDefaultImage(skuEntity.getDefaultImage());

        //根据cid3查询分类信息
        ResponseVo<List<ItemCategoryVo>> listResponseVo = this.gmallPmsClient.queryCategoriesVoByCid3(skuEntity.getCategoryId());
        List<ItemCategoryVo> itemCategoryVoList = listResponseVo.getData();
        itemVo.setCategories(itemCategoryVoList);


        //根据品牌的id查询品牌
        ResponseVo<BrandEntity> brandEntityResponseVo = this.gmallPmsClient.queryBrandById(skuEntity.getBrandId());
        BrandEntity brandEntity = brandEntityResponseVo.getData();
        if(brandEntity!=null){
            itemVo.setBrandId(brandEntity.getId());
            itemVo.setBrandName(brandEntity.getName());
        }

        //根据skuid查询spu
        ResponseVo<SpuEntity> spuEntityResponseVo = this.gmallPmsClient.querySpuById(skuEntity.getSpuId());
        SpuEntity spuEntity = spuEntityResponseVo.getData();
        if(spuEntity!=null){

            itemVo.setSpuName(spuEntity.getName());
            itemVo.setSpuId(spuEntity.getId());
        }

        //根据skuid查询
        ResponseVo<List<SkuImagesEntity>> skuImagesResponseVo = this.gmallPmsClient.queryImagesBySkuId(skuId);
        List<SkuImagesEntity> skuImagesEntities = skuImagesResponseVo.getData();
        itemVo.setImages(skuImagesEntities);


        //根据skuid查询sku营销信息
        ResponseVo<List<ItemSaleVo>> itemSaleVoRespnseVo = this.gmallSmsClient.querySalesBySkuId(skuId);


        //根据skuid查询
        ResponseVo<List<WareSkuEntity>> wareSkuEntitiesResponseVo = this.gmallWmsClient.queryWareSkusBySkuId(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuEntitiesResponseVo.getData();
        if(!CollectionUtils.isEmpty(wareSkuEntities)){
          itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()-wareSkuEntity.getStockLocked() >0));
        }

        //查询spu下的所有
        ResponseVo<List<SkuAttrValueEntity>> skuAttrResponseVo = this.gmallPmsClient.querySkuAttrValuesBySpuId(skuEntity.getSpuId());
        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrResponseVo.getData();
        if(!CollectionUtils.isEmpty(skuAttrValueEntities)){
            List<AttrValueVo> saleAttrs = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
               AttrValueVo attrValueVo = new AttrValueVo();
                BeanUtils.copyProperties(skuAttrValueEntity,attrValueVo);
                return attrValueVo;
            }).collect(Collectors.toList());
        }

        //根据spuId查询spu的海报信息
        ResponseVo<SpuDescEntity> spuDescEntityResponseVo = this.gmallPmsClient.querySpuDescById(skuEntity.getSpuId());
        SpuDescEntity spuDescEntity = spuDescEntityResponseVo.getData();
        //先判断是否是空 然后转为集合 2021年5月28日15:00:41
        if(spuDescEntity!=null&& StringUtils.isNoneBlank(spuDescEntity.getDecript())){
            String[] images = StringUtils.split(spuDescEntity.getDecript(), ',');
            itemVo.setSpuIamges(Arrays.asList(images));
        }


        return null;
    }
}
