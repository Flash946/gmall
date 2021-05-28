package com.atguigu.gmall.sms.controller;

import java.util.List;

import com.atguigu.gmall.sms.vo.SaleVo;
import com.latguigu.gmall.sms.vo.ItemSaleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品spu积分设置
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-05-13 10:48:21
 */
@Api(tags = "商品spu积分设置 管理")
@RestController
@RequestMapping("sms/skubounds")
public class SkuBoundsController {

    @Autowired
    private SkuBoundsService skuBoundsService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = skuBoundsService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuBoundsEntity> querySpuBoundsById(@PathVariable("id") Long id){
		SkuBoundsEntity spuBounds = skuBoundsService.getById(id);

        return ResponseVo.ok(spuBounds);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuBoundsEntity spuBounds){
		skuBoundsService.save(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuBoundsEntity spuBounds){
		skuBoundsService.updateById(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuBoundsService.removeByIds(ids);

        return ResponseVo.ok();
    }


    /* 下面是我新写的代码*/
    @PostMapping("/sale")
    public ResponseVo saveSales(@RequestBody SaleVo saleVo){
        skuBoundsService.saveSaleVo(saleVo);

        return ResponseVo.ok();
    }

    /**
     *
     * @param skuId
     * @return
     */
    @GetMapping("sku/{skuId}")
    public ResponseVo<List<ItemSaleVo>> querySalesBySkuId(@PathVariable("skuId")Long skuId){
        List<ItemSaleVo> itemSaleVos = this.skuBoundsService.querySalesBySkuId(skuId);
        return ResponseVo.ok(itemSaleVos);
    }
//
//    @GetMapping("sku/{skuId}")
//    public ResponseVo<ItemSaleVo> list(PageParamVo paramVo){
//        PageResultVo pageResultVo = skuBoundsService.queryPage(paramVo);
//
//        return ResponseVo.ok(pageResultVo);
//    }
}
