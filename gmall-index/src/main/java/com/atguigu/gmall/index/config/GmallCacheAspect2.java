/*
package com.atguigu.gmall.index.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;

@Aspect
public class GmallCacheAspect2 {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    */
/**
     * joinPoint.getArgs() 获取方法参数
     * (MethodSignature) joinPoint.getSignature() 获取方法签名
     * joinPoint.getTarget().getClass() 获取目标方法的所在类
     * @param joinPoint
     * @return
     *//*

    @Around("@annotation(com.atguigu.gmall.index.config.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //通过方法签名获取缓存注解对象
        GmallCache cache = signature.getMethod().getAnnotation(GmallCache.class);

        Class<?> aClass = joinPoint.getTarget().getClass();
        //Object[] args = joinPoint.getArgs();
        String args = Arrays.asList(joinPoint.getArgs()).toString();


        //获取缓存前缀
        String prefix = cache.prefix();
        String lock = cache.lock();

        //查询缓存,判断缓存中是否存在
        this.redisTemplate.opsForValue().get(lock);
        //缓存中没有，加分布式锁
        int timeout = cache.timeout();
        int random = cache.random();
        this.redisTemplate.opsForValue().get("")
        //查询缓存，判断缓存中是否存在

        //执行目标方法
        return null;
    }

}
*/
