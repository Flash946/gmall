package com.atguigu.guli.item;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestCompleteFuture {
    public static void main(String[] args) {
        testP10();
    }

    public void testP7AndP8(){
        CompletableFuture.runAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:runAsync");
        });
        CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:supplyAsync");
            return "return supplyAsync";
        });

        CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:supplyAsync");
//            int i = 1/0;
            return "return supplyAsync";
        }).whenComplete((s, throwable) -> {
            System.out.println("===============whenComplete======start=======");
            System.out.println("上一个任务的返回值:s:"+s);
            System.out.println("throwable:"+throwable);
            int i = 1/0;
            System.out.println("===============whenComplete======end=======");
        }).exceptionally(throwable -> {
            System.out.println("===============exceptionally======start=======");
            System.out.println("上一个任务的异常信息:throwable:"+throwable);
            System.out.println("===============exceptionally======end=======");

            return  "hello exceptionally";

        });
    }

    /**
     * day13 p9 0-11min 演示代码
     */
    public static void testP9(){
        CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:supplyAsync");
            return "return supplyAsync";
        }).thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync======start=======");
            System.out.println("上一个任务的返回值:s:"+s);
            System.out.println("===============thenApplyAsync======end=======");
            return "hello,thenApplyAsync";

        }).thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync2======start=======");
            System.out.println("上一个任务的返回值:s:"+s);
            System.out.println("===============thenApplyAsync2======end=======");
            return "hello,thenApplyAsync2";
        }).thenAcceptAsync(t->{
            System.out.println("===============thenAcceptAsync======start=======");
            System.out.println("上一个任务的返回值:t:"+t);
            System.out.println("===============thenAcceptAsync======end=======");
        }).thenRunAsync(() -> {
            System.out.println("===============thenRunAsync======start=======");
            System.out.println("thenRunAsync不获取结果,也没有自己的返回结果");
            System.out.println("===============thenRunAsync======end=======");
        });
    }


    /**
     * DAY13 P9 11MIN-19min end
     */
    public static void testP9_2(){
        CompletableFuture<String> aFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:supplyAsync");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("supplyAsync sellep end");

            return "return supplyAsync";
        });
        aFuture.thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync======start=======");
            System.out.println("thenApplyAsync上一个任务的返回值:s:"+s);
            System.out.println("===============thenApplyAsync======end=======");
            return "hello,thenApplyAsync";

        });

        aFuture.thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync2======start=======");
            System.out.println("thenApplyAsync2上一个任务的返回值:s:"+s);
            System.out.println("===============thenApplyAsync2======end=======");
            return "hello,thenApplyAsync2";
        });
        aFuture.thenAcceptAsync(t->{
            System.out.println("===============thenAcceptAsync======start=======");
            System.out.println("thenAcceptAsync上一个任务的返回值:t:"+t);
            System.out.println("===============thenAcceptAsync======end=======");
        });
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * DAY13 P9 11MIN-19min end
     */
    public static void testP10(){
        CompletableFuture<String> aFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompleteableFuture子任务:supplyAsync");


            return "return supplyAsync";
        });
        CompletableFuture<String> bFuture = aFuture.thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync======start=======");
            System.out.println("thenApplyAsync上一个任务的返回值:s:" + s);
            System.out.println("===============thenApplyAsync======end=======");
            return "hello,thenApplyAsync";

        });

        CompletableFuture<String> cFuture =aFuture.thenApplyAsync(s -> {
            System.out.println("===============thenApplyAsync2======start=======");
            System.out.println("thenApplyAsync2上一个任务的返回值:s:"+s);
            System.out.println("===============thenApplyAsync2======end=======");
            return "hello,thenApplyAsync2";
        });
        CompletableFuture<Void> dFuture = aFuture.thenAcceptAsync(t->{
            System.out.println("===============thenAcceptAsync======start=======");
            System.out.println("thenAcceptAsync上一个任务的返回值:t:"+t);
            System.out.println("===============thenAcceptAsync======end=======");
        });
        CompletableFuture.allOf(bFuture,cFuture,dFuture).join();
        System.out.println("主线程执行完！");

//        try {
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
