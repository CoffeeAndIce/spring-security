package cn.coffeeandice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.DenyAll;

/**
 * @author : CoffeeAndIce
 * @Todo: 测试登入效果，默认情况下（不自定义端点过滤），会根据注解判断权限。否则会根据自定义端点判断权限从而触发相应处理类
 */
@RestController
@RequestMapping("/test")
public class AdminController {

    @GetMapping("/demo")
    public String demo() {
        return "测试登入";
    }

//    @DenyAll
    @GetMapping("/right")
    public String right() {
        return "测试不存在权限";
    }
}
