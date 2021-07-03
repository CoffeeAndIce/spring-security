package cn.coffeeandice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
//                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .passwordEncoder(CustomePasswordEncoder.getInstance())
                // <C> 配置 admin 用户
                .withUser("admin").password("admin").roles("ADMIN")
                // <C> 配置 normal 用户
                .and().withUser("normal").password("normal").roles("NORMAL");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // <A> 配置请求地址的权限
                .authorizeRequests()
                .antMatchers("/test/demo").permitAll() // 所有用户可访问
                .antMatchers("/test/admin").hasRole("ADMIN") // 需要 ADMIN 角色
                .antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')") // 需要 NORMAL 角色。
                // 任何请求，访问的用户都需要经过认证
                .anyRequest().authenticated()
                .and()
                // <B> 设置 Form 表单登录
                .formLogin()
//                    .loginPage("/login") // 登录 URL 地址
                .permitAll() // 所有用户可访问
                .and()
                // <C> 配置退出相关
                .logout()
//                    .logoutUrl("/logout") // 退出 URL 地址
                .permitAll(); // 所有用户可访问
    }
}
