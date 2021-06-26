package cn.coffeeandice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : CoffeeAndIce
 * @Todo: 测试登入效果
 */
@RestController
@RequestMapping("/test")
public class AdminController {

    @GetMapping("/demo")
    public String demo() {
        return "测试登入";
    }

}
