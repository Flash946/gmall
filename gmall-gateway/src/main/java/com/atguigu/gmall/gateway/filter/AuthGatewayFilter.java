package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGatewayFilter implements GatewayFilter {

    @Autowired
    private  JwtProperties jwtProperties;
    /**
     * GatewayFilterChain 执行链
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request对象  HttpServletRequest (servelt提供的) --> ServerHttpRequest
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
       // 2021年6月1日16:05:33  本来接受不到copokie(就算有)的。反向代理了http://manager.gmall.com/可以了。
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if(CollectionUtils.isEmpty(cookies)||!cookies.containsKey(jwtProperties.getCookieName())){
            //拦截
            response.setStatusCode(HttpStatus.UNAUTHORIZED);//401
            return response.setComplete();
        }
        try {
            //1、获取jwt(在cookie中)
            HttpCookie cookie = cookies.getFirst(jwtProperties.getCookieName());
            String token = cookie.getValue();
            //2. 判断token是否为空
            if(StringUtils.isEmpty(token)){
                //拦截
                response.setStatusCode(HttpStatus.UNAUTHORIZED);//401
                return response.setComplete();
            }

            //3、解析jwt
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());

            //5. 放行
            return chain.filter(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            //4、异常，则去登录
            response.setStatusCode(HttpStatus.UNAUTHORIZED);//401
            return response.setComplete();
        }
    }
}
