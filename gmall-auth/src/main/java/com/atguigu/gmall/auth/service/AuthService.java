package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.UserException;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
//开启配置类
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String username, String password) {
        try {
            //1、完成远程请求，获取用户信息
            ResponseVo<UserEntity> userEntityResponseVo = this.gmallUmsClient.queryUser(username, password);
            UserEntity userEntity = userEntityResponseVo.getData();

            //2、判断用户信息是否为空
            if(userEntity==null){
                throw new UserException("用户名或者密码错误");
            }
            //3、制作jwt类型的cookie信息
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userEntity.getId());
            map.put("username",userEntity.getUsername());
            String token = JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            //throw new UserException("用户名或者密码错误");
        }
        return  null;
    }
}
