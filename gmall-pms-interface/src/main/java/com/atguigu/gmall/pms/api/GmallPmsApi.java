package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {

    @PostMapping("pms/spu/page")
    public ResponseVo<List<SpuEntity>> querySpuByPage(@RequestBody PageParamVo paramVo);

    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> querySkuBySquId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);


    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/attr/category/{cid}")
    public ResponseVo<List<AttrEntity>> queryAttrByCid(@PathVariable("cid") Long cid, @RequestParam(value = "type",required = false) Integer type, @RequestParam(value = "searchType",required = false) Integer searchType);


    @GetMapping("pms/skuattrvalue/search/attr")
    @ApiOperation("day8-p5-45min")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuSearchAttrValue(@RequestParam("skuId") Long skuId,@RequestParam("attrIds") List<Long> attrIds);

    /**
     * 2021年5月22日18:40:27  问号只能通过注解接收
     * @param spuId
     * @param attrIds
     * @return
     */
    @GetMapping("pms/spuattrvalue/search/attr")
    @ApiOperation("day8-p5-45min")
    public ResponseVo<List<SpuAttrValueEntity>> querySpuSearchAttrValue(@RequestParam("spuId") Long spuId, @RequestParam("attrIds") List<Long> attrIds);


    /* =============================================== 2021年5月27日14:30:58 =================*/
    @GetMapping("pms/category/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByPid(@PathVariable("parentId")Long pid);

    @GetMapping("pms/category/subs/{pid}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesWithSub(@PathVariable("pid")Long pid);


    //=============================== day13 商品详情所需=============== 2021年5月27日21:05:32 ===========
    /**
     * 信息
     * day13 商品详情所需
     */
    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);
    /**
     * 查询图片 2021年5月27日21:11:10
     * day13 商品详情所需
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuimages/sku/{skuId}")
    public ResponseVo<List<SkuImagesEntity>> queryImagesBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 信息
     * day13 商品详情所需
     */
    @GetMapping("pms/skuimages/{spuId}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);
    /**
     * 根据三级分类
     * 2021年5月27日21:22:29
     * @param cid3
     * @return
     */
    @GetMapping("pms/category/all/{cid3}")
    public ResponseVo<List<ItemCategoryVo>> queryCategoriesVoByCid3(@PathVariable("cid3") Long cid3);


    /**
     * day13 p5 2021年5月28日14:49:43  用到  但我没写！复制过来了
     * @param spuId
     * @return
     */
    @GetMapping("spu/{spuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuAttrValuesBySpuId(@PathVariable("spuId")Long spuId);

}
