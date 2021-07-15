package cn.coffeeandice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

/**
 * @author : CoffeeAndIce
 * @Todo: 测试登入效果
 */
@RestController
@RequestMapping("/api")
public class ResourceController {

    @GetMapping("/hello")
    public String demo() {
        return "world";
    }

}
