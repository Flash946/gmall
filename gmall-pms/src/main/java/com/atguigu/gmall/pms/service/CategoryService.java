package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.CategoryVo;
import com.atguigu.gmall.pms.entity.ItemCategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-05-13 10:00:52
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<CategoryEntity> queryCatagoryById(Long id);

    List<CategoryVo> queryCategoryVoByPid(Long pid);

    List<ItemCategoryVo> queryCategoryVoByCid3(Long cid3);
}


