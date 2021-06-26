package cn.coffeeandice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/**
 * @author : CoffeeAndIce
 * @Todo: 配置内存性用户角色，不使用密码编码
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                // <A> 使用内存中的 InMemoryUserDetailsManager
                        inMemoryAuthentication()
                // <B> 不使用 PasswordEncoder 密码编码器
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                // <C> 配置 admin 用户
                .withUser("admin").password("admin").roles("ADMIN")
                // <C> 配置 normal 用户
                .and().withUser("normal").password("normal").roles("NORMAL");
    }
}
