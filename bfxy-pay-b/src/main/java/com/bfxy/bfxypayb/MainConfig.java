package com.bfxy.bfxypayb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：配置类扫描
 *
 * @author xielei
 * @date 2019/07/01
 */
@Configuration
@ComponentScan(value = {"com.bfxy.bfxypayb.*","com.bfxy.bfxypayb.config.*"})
@MapperScan(value = {"com.bfxy.bfxyorder.mapper"})
public class MainConfig {
}
