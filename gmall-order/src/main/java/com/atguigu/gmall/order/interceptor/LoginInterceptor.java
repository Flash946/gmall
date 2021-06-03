package com.atguigu.gmall.order.interceptor;

import com.atguigu.gmall.common.bean.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
//@Scope("protetype")
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

//    private static UserInfo userInfo;

    private final int test = 0;

    private static  final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 前置 方法
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      UserInfo  userInfo = new UserInfo();

        try {
            //1.获取cookie信息(userkey token)
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

            //4.解析jwt类型的token,获取用户信息
            Map<String, Object> map = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            Long userId = Long.valueOf( map.get("userId").toString());
            //5.把userkey和user传递给后续的业务逻辑(controller service map)
            userInfo.setUserId(userId);

            //不管有没有登录都要放行：登录-获取userId 未登录-获取userKey
            THREAD_LOCAL.set(userInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果解析过程中出现异常需要拦截
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.error("jwt解析出错");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在视图渲染之后执行，经常在完成方法中释放资源。
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //此处删除方法必须调用。因为使用的是tomcat线程池，请求结束后，线程不会结束。
        //如果不手动删除线程变量，可能会导致内存泄漏。
        THREAD_LOCAL.remove();
    }


    public  static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }
}
