package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("accredit")
    public ResponseVo<Object> accredit(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String token = this.authService.accredit(username, password);
        //cookie属于表现层的操作 所以一般不在service里完成。
        //4、放入
        // cookie中，响应给浏览器
        if(StringUtils.isNoneBlank(token)){
            CookieUtils.setCookie(httpServletRequest,httpServletResponse,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()* 60);
            return ResponseVo.ok();
        }
        return ResponseVo.fail();

    }
}
