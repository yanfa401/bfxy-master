package com.bfxy.bfxyorder.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：测试
 *
 * @author xielei
 * @date 2019/07/01
 */

@RestController
public class IndexController {
    
    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
