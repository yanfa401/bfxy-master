package com.bfxy.bfxystore.service.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.bfxy.store.service.api.HelloServiceApi;

/**
 * 描述：
 *
 * @author xielei
 * @date 2019/07/03
 */

@Service(
        version = "0.0.1",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class HelloServiceImpl implements HelloServiceApi {
    
    /**
     * 测试接口
     *
     * @param name
     * @return
     */
    @Override
    public String sayHello(String name) {
        return "收到name:"+name;
    }
}
