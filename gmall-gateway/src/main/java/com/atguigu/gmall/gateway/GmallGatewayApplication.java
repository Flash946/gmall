package com.atguigu.gmall.gateway;

import com.atguigu.gmall.gateway.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.cors.reactive.CorsWebFilter;

@SpringBootApplication
public class GmallGatewayApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ac;
        ac =  SpringApplication.run(GmallGatewayApplication.class, args);
        System.out.println("GmallGatewayApplication.ac="+ac);
        CorsConfig bean = ac.getBean(CorsConfig.class);
        CorsWebFilter bean2 = ac.getBean(CorsWebFilter.class);

        System.out.println("bean2="+bean2);
        System.out.println("bean="+bean.corsWebFilter());
    }

}
