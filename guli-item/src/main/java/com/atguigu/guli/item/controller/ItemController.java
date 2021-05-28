package com.atguigu.guli.item.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.guli.item.service.ItemService;
import com.atguigu.guli.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;
    @GetMapping("")
    public ResponseVo<ItemVo> load(@PathVariable("skuId") Long skuId){
       ItemVo itemVo = this.itemService.load(skuId);
       return ResponseVo.ok(itemVo);
    }
}
