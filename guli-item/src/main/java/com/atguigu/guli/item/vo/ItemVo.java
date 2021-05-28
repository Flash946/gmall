package com.atguigu.guli.item.vo;

import com.atguigu.gmall.pms.entity.ItemCategoryVo;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemVo {
    //三级分类
    private List<ItemCategoryVo> categories;

    //品牌
    private  Long brandId;
    private String brandName;

    //spu
    private Long spuId;
    private String spuName;

    //sku Y
    private Long skuId;
    private String title;
    private String subTitle;
    private BigDecimal price;
    private Integer weight;//重量。快递费跟之有关，所以可能被计算 用这个类型
    private String defaultImage;


    //营销信息
    private List<ItemSaleVo> sales; //
    //是否有货
    private Boolean store;

    private List<AttrValueVo> saleAttrs;//sku所属spu下的所有sku的销售属性
    //sku图片
    private List<SkuImagesEntity> images;
    private List<String> spuIamges;//spu的海报信息
    private List<ItemGroupVo> groups; //规格参数组下的规格参数
}
