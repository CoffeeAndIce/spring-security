package cn.coffeeandice.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : CoffeeAndIce
 * @Todo:
 * @Date: 2021/07/04
 */

public class CustomePasswordEncoder implements PasswordEncoder {


    public CustomePasswordEncoder() {
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }

}
