package com.bfxy.bfxyorder.web;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bfxy.store.service.api.HelloServiceApi;
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
    
    
    @HystrixCommand(
            commandKey = "/testHystrix",
            fallbackMethod = "testHystrixFallBack",
            commandProperties = {
                    //是否启动超时降级
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    //超时时间
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            }
    )
    @GetMapping("/testHystrix")
    public String testHystrix() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        return "下单成功";
    }
    
    public String testHystrixFallBack() throws InterruptedException {
        return "降级了";
    }
}
