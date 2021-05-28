package com.latguigu.gmall.sms.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleVo {

    //2021年5月15日17:32:56  这个我想到了吗
    private Long skuId;

    // sku积分相关信息
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // sku满减信息
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    // sku打折信息
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;



}
