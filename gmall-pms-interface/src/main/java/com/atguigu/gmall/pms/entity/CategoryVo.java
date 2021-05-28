package com.atguigu.gmall.pms.entity;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo extends  CategoryEntity{
    private List<CategoryEntity> subs;
}
