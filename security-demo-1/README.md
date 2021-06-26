### 	基于内存的基础spring-securtiy 样例 
 [返回主文档](../README.md)
#### 一、默认情况下配置spring-security配置
> 会自动基于user为用户名，基于`UUID.randomUUID().toString()`生成随机 密码


自动配置方法：[UserDetailsServiceAutoConfiguration](#UserDetailsServiceAutoConfiguration)



#### 二、自定义配置账户权限（认证管理器）
> 下属方式中，代码大于配置。默认启动内存模式认证管理器

属性配置类：[SecurityProperties](#SecurityProperties)

用户凭证管理：[UserDetailsManager](../README.md#UserDetailsManager)



##### 1、基于yaml/yml方式
> 当且仅当只有一个用户
````xml
spring:
  # Spring Security 配置项，对应 SecurityProperties 配置类
  security:
    # 配置默认的 InMemoryUserDetailsManager 的用户账号与密码。
    user:
      name: user # 账号
      password: user # 密码
      roles: ADMIN # 拥有角色

````



##### 2、基于代码方式
> 基于配置，可以设置多用户

````java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //<A>基于认证管理器配置来配置多个用户
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                // <B> 使用内存中的 InMemoryUserDetailsManager（认证管理器）
                        inMemoryAuthentication()
                // <C> 不使用 PasswordEncoder 密码编码器(虽然不建议使用，但是并没有在下个版本中计划删除，不慌）
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                // <D> 配置 admin 用户
                .withUser("user").password("user").roles("ADMIN")
                // <D> 配置 normal 用户
                .and().withUser("normal").password("normal").roles("NORMAL");
    }
}

````



#### 相关全路径地址：

###### <span id="UserDetailsServiceAutoConfiguration">1. UserDetailsServiceAutoConfiguration</span>
>`org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration`


###### <span id="UserDetailsServiceAutoConfiguration">2. SecurityProperties</span>
>`org.springframework.boot.autoconfigure.security.SecurityProperties`

