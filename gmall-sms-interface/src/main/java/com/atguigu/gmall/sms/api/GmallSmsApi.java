package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.latguigu.gmall.sms.vo.ItemSaleVo;
import com.latguigu.gmall.sms.vo.SaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sale")
    public ResponseVo<Object> saveSales(@RequestBody SaleVo saleVo);

    /**
     * 2021年5月28日14:41:46
     * @param skuId
     * @return
     */
    @GetMapping("sku/{skuId}")
    public ResponseVo<List<ItemSaleVo>> querySalesBySkuId(@PathVariable("skuId")Long skuId);
}
