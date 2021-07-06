package cn.coffeeandice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

/**
 * @author : CoffeeAndIce
 */
@SpringBootApplication
@EnableMongoHttpSession
public class SecurityApplication_Demo_3_MongoDb {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication_Demo_3_MongoDb.class, args);
    }

}
