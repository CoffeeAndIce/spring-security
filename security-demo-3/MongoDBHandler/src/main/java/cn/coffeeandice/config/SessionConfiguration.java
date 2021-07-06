package cn.coffeeandice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.JacksonMongoSessionConverter;

/**
 * @author : CoffeeAndIce
 * @Todo:
 * @Date: 2021/07/07
 */
@Configuration
public class SessionConfiguration {

    @Bean
    public AbstractMongoSessionConverter mongoSessionConverter() {
        return new JacksonMongoSessionConverter();
    }
}
