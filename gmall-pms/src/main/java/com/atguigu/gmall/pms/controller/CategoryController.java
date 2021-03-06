package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.entity.CategoryVo;
import com.atguigu.gmall.pms.entity.ItemCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品三级分类
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-05-13 10:00:52
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id){
		CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

    @GetMapping("/parent/{parenrId}")
    public ResponseVo<List<CategoryEntity>>  queryCatagoryById(@PathVariable("parenrId") Long id){
        List<CategoryEntity>  categoryEntityList= categoryService.queryCatagoryById(id);
        return  ResponseVo.ok(categoryEntityList);
    }
    //============================================
    /**
     * 新增方法 day11 p6
     */
    @GetMapping("/lv11/{pid}")
    public ResponseVo<List<CategoryVo>> queryCategoryVoByPid(@PathVariable("pid") Long pid){
        List<CategoryVo> categoryVos =   this.categoryService.queryCategoryVoByPid(pid);
        return ResponseVo.ok(categoryVos);
    }

    /**
     * 根据三级分类
     * 2021年5月27日21:22:29
     * @param cid3
     * @return
     */
    @GetMapping("/all/{cid3}")
    public ResponseVo<List<ItemCategoryVo>> queryCategoryVoByCid3(@PathVariable("cid3") Long cid3){
        List<ItemCategoryVo> itemCategoryVos =   this.categoryService.queryCategoryVoByCid3(cid3);
        return ResponseVo.ok(itemCategoryVos);
    }
}
