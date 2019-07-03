package com.bfxy.bfxyorder.config.hystrix;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

/**
 * 描述：熔断器配置类
 *
 * @author xielei
 * @date 2019/07/03
 */

@Configuration
public class HystrixConfig {
    
    /**
     * 用来拦截处理 HystrixCommand 注解
     * @return
     */
    @Bean
    public HystrixCommandAspect getHystrixCommandAspect(){
        return new HystrixCommandAspect();
    }
    
    /**
     * 用来像监控中心 Dashboard发送 stream信息
     * @return
     */
    @Bean
    public ServletRegistrationBean getServletRegistrationBean(){
        ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registration.addUrlMappings("/hystrix.stream");
        return registration;
    }
}
