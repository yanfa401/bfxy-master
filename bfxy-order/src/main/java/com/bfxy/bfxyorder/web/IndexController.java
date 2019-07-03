package com.bfxy.bfxyorder.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bfxy.store.service.api.HelloServiceApi;

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
    
    @GetMapping("/index/{name}")
    public String index(@PathVariable(value = "name") String name) {
        return helloServiceApi.sayHello(name);
    }
}
