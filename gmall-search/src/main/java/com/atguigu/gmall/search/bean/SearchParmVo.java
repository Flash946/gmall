package com.atguigu.gmall.search.bean;


import lombok.Data;

import java.util.List;

/**
 * 接收页面传递过来的检索参数
 * search?keyword=小米&brandId=1,3&cid=255&props=5:高通-麒麟,6:骁龙-硅谷1000&sort=1:desc&priceFrom=1000&priceTo=6000&pageNum=
 * 2021年5月25日20:40:52
 */
@Data
public class SearchParmVo {
    //检索条件
    private String keyword;
    //品牌过滤
    private List<Long> brandId;
    //商品分类
    private String cid;
    //过滤的检索参数
    private  List<String> props;

    private  String sort; //排序字段 1:desc(1-price 2-createtim 3-sales desc 降序  asc升序)

    private  double priceFrom;
    private  double priceTo;

    private Boolean store; //是否有货

    private Integer pageNum =1;
    //这个写死 有原因的。 前端展示及不方便爬数据
    private final Integer pageSize= 20;


}
