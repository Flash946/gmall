package com.atguigu.guli.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

@SpringBootTest
class GuliItemApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        new MyThread().start();

//        new Thread(new MyRunnable()).start();

//        FutureTask futureTask = new FutureTask<>(new MyCallable());
//        new Thread(futureTask).start();
        //获取返回值
//        System.out.println(futureTask.get());

        //阿里不推荐这个 容易OOM
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(() -> {
            System.out.println("这是线程池工具类的方式初始化子线程程序");
        });

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500));
        threadPoolExecutor.execute(() -> {
            System.out.println("这是线程池构造方法的方式初始化子线程程序");
        });
    }





}
class MyCallable implements Callable{

    /**
     * 可以抛异常给主线程
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        System.out.println("这是 Callable 接口的方式实现多线程");

        return "Callable 返回值";
    }
}
class MyRunnable implements  Runnable{

    @Override
    public void run() {
        System.out.println("这是Runnable接口的方式实现多线程");
    }
}


class MyThread extends  Thread{
    @Override
    public void run() {
        System.out.println("这是Thread方式实现多线程");
    }
}