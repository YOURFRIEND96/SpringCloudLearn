package com.abc.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
    })
    public String paymentCircuitBreaker(@PathVariable("id")Integer id)
    {
        if (id < 0) {
            throw new RuntimeException("*******id 不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();

        return Thread.currentThread().getName()+"\t"+"调用成功，流水号： "+serialNumber;
    }

    public String paymentCircuitBreaker_fallback(@PathVariable("id")Integer id)
    {
        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
    }


    public String paymentInfo_OK(Integer id)
    {
        return "线程池： "+Thread.currentThread().getName()+" paymentInfo_ok,id: "+id+"\t"+"O(∩_∩)O哈哈~";

    }

    @HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler", commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
    })
    public String paymentInfo_Timeout(Integer id)
    {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：  "+Thread.currentThread().getName()+" paymentInfo_Timeout,id: "+id+"\t"+"O(∩_∩)O哈哈~";
    }

    // 用来善后的方法
    public String paymentInfo_TimeoutHandler(Integer id)
    {
        return "线程池：  "+Thread.currentThread().getName()+"8001系统繁忙或者运行报错，请稍后再试，id: "+id+"\t"+"o(╥﹏╥)o";
    }
}
