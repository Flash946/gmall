package com.atguigu.gmall.search.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.search.bean.SearchParmVo;


import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("search")
public class SearchController {
    @Autowired
   private SearchService searchService;
    @GetMapping
    public ResponseVo<Object> search(SearchParmVo searchParmVo){
         searchService.search(searchParmVo);
        return  ResponseVo.ok();
    }
}
