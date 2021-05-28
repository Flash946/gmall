package com.atguigu.gmall.pms.com.atguigu.gmall.pms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public HikariDataSource hikariDataSource(@Value("spring.datasource.url")String url) {
//
//        return hikariDataSource;
//    }
//
//    /**
//     * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
//     *
//     * @param hikariDataSource The HikariDataSource
//     * @return The default datasource
//     */
//    @Primary
//    @Bean("dataSource")
//    public DataSource dataSource(@Value("spring.datasource.url")String url,@Value("spring.datasource.url")String url.
//            @Value("spring.datasource.url")String url,
//                                 @Value("spring.datasource.url")String url) {
//        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.setJdbcUrl(url);
//        return new DataSourceProxy(hikariDataSource);
//    }
}
