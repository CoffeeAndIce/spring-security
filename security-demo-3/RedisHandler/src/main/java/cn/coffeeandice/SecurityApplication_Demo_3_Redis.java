package cn.coffeeandice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author : CoffeeAndIce
 */
@SpringBootApplication
@EnableRedisHttpSession
public class SecurityApplication_Demo_3_Redis {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication_Demo_3_Redis.class, args);
    }

}
