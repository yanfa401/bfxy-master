package com.bfxy.bfxyorder.web;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bfxy.bfxyorder.entity.po.Order;
import com.bfxy.store.service.api.HelloServiceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * 描述：测试
 *
 * @author xielei
 * @date 2019/07/01
 */

@RestController
public class IndexController {
    
    private AtomicLong counter = new AtomicLong();
    private AtomicLong errorCounter = new AtomicLong();
    
    @Reference(
            version = "0.0.1",
            application = "${dubbo.application.id}",
            interfaceClass = HelloServiceApi.class,
            check = false,
            timeout = 3000,//比较吉利
            retries = 0 //如果provider是写操作,没做幂等性0次,读操作最多重试3次
    )
    private HelloServiceApi helloServiceApi;
    
    
    @HystrixCommand(
            commandKey = "/index",
            fallbackMethod = "indexFallBack",
            commandProperties = {
                    //是否启动超时降级
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    //超时时间
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            }
    )
    @GetMapping("/index/{name}")
    public String index(@PathVariable(value = "name") String name) {
        return helloServiceApi.sayHello(name);
    }
    
    public String indexFallBack(@PathVariable(value = "name") String name) {
        return "dubbo访问失败";
    }
    
    
    /* 超时时间
    @HystrixCommand(
            commandKey = "testHystrix",
            fallbackMethod = "testHystrixFallBack",
            commandProperties = {
                    //是否启动超时降级
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    //超时时间
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            }
    )*/
    
    /* 超时时间+线程池限流
    @HystrixCommand(
            commandKey = "testHystrix",
            fallbackMethod = "testHystrixFallBack",
            commandProperties = {
                    //启用线程池
                    @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
                    //是否启动超时降级
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    //超时时间
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            },
            threadPoolKey = "testHystrixThreadPoolKey",
            threadPoolProperties = {
                    @HystrixProperty(name="coreSize", value="10"),//并发执行的最大线程数，默认10
                    @HystrixProperty(name="maxQueueSize", value="20000"),//BlockingQueue的最大队列数
                    @HystrixProperty(name="queueSizeRejectionThreshold", value="30"),//即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝
            }
    )*/
    
   /* 超时时间+信号量限流
   @HystrixCommand(
            commandKey = "testHystrix",
            fallbackMethod = "testHystrixFallBack",
            commandProperties = {
                    //启用信号量
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    //信号量的最大并发请求数
                    @HystrixProperty(name="execution.isolation.semaphore.maxConcurrentRequests", value="10"),
                    //是否启动超时降级
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    //超时时间
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            }
    )*/
   
    @GetMapping("/testHystrix")
    public String testHystrix() throws InterruptedException {
        System.out.println("正常逻辑:"+counter.incrementAndGet());
//        TimeUnit.SECONDS.sleep(1L);
        return "下单成功";
    }
    
    public String testHystrixFallBack() throws InterruptedException {
        System.out.println("异常逻辑:"+errorCounter.incrementAndGet());
        return "降级了";
    }
    
    
    @HystrixCollapser(
            batchMethod = "batchFindById",
            collapserKey = "findByIdCollapserKey",
            collapserProperties = {
                    //单个请求的延迟时间
                    @HystrixProperty(name = "timerDelayInMilliseconds", value = "200"),
                    //允许最大的合并请求数量
                    @HystrixProperty(name = "maxRequestsInBatch", value = "50"),
                    //是否开启request的本地缓存(一般建false)
                    @HystrixProperty(name = "requestCache.enabled", value = "false"),
            }
    )
    @GetMapping("/findById/{id}")
    public Future<Order> findById(@PathVariable("id") String id){
        return null;
    }
    
    
    @HystrixCommand(fallbackMethod = "findAllFallBack")
    public List<Order> findAll(List<String> ids){
        RestTemplate restTemplate = new RestTemplate();
        List<Order> orders
                = restTemplate.getForObject("", List.class, StringUtils.join(ids, ","));
        return orders;
    }
    
    
    public List<Order> findAllFallBack(List<String> ids){
        System.err.println("降级");
        return Collections.emptyList();
    }
    
}
