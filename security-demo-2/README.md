### 基于内存的接口权限控制 样例 
 [返回主文档](../README.md)
#### 一、配置基于URl(EndPoint)的权限控制
> 通过配置 `HttpSecurity`达到基于url的权限控制

#### 二、自定义配置访问控制
> 下属方式中，代码大于配置

````java
# SecurityConfig.java
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
//                    .loginPage("/login") // 可配置登录 URL 地址
                .permitAll() // 所有用户可访问
                .and()
                // <C> 配置退出相关
                .logout()
//                    .logoutUrl("/logout") // 可配置退出 URL 地址
                .permitAll(); // 所有用户可访问
    }

````


#### 三、如何处理权限不足的问题？
````java
    ...
    //上述配置中继续添加and()
    and().exceptionHandling()
       （1）可以通过 .accessDeniedHandler() 配置处理权限拒绝的问题
             内部存在 HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e 可以处理
       （2）可以通过 .accessDeniedPage()配置错误页面
````



[配置代码中权限相关解释](../README.md#ExpressionUrlAuthorizationConfigurer)


#### 四、自定义编码器
> 整体是实现PasswordEncoder接口，根据自己的方式方法去实现自己的编码规则。
> 例子中参照`NoOpPasswordEncoder` 做一个不编码的示例编码器

[示例路径地址](./src/main/java/cn/coffeeandice/config/CustomePasswordEncoder.java)


##### 1、重新定义工厂类
````java
@Bean
PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
````

````java
//基于原有加密方法情况下加入自定义代码
public class PasswordEncoderFactories {

   public static PasswordEncoder createDelegatingPasswordEncoder() {
      String encodingId = "bcrypt";
      Map<String, PasswordEncoder> encoders = new HashMap<>();
      encoders.put(encodingId, new BCryptPasswordEncoder());
      encoders.put("ldap", new LdapShaPasswordEncoder());
      encoders.put("MD4", new Md4PasswordEncoder());
      encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
      encoders.put("noop", NoOpPasswordEncoder.getInstance());
      encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
      encoders.put("scrypt", new SCryptPasswordEncoder());
      encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
      encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
      encoders.put("sha256", new StandardPasswordEncoder());


      encoders.put("customer", CustomerPasswordEncoder());
      return new DelegatingPasswordEncoder(encodingId, encoders);
   }

   private PasswordEncoderFactories() {}
}
````



#### 五、常用权限注解（3个）

> 实际运用上，我们需要对资源进行管理，搭配自己设定的权限内容进行限定（也就是authorities）,说到底也是整合了spring-security的方式

##### @Secured()

> 在任意配置类上增加@EnableGlobalMethodSecurity(securedEnabled = true),角色上需要附带前缀 `ROLE_`，否则直接不予辨认 

```java
//AbstractAccessDecisionManager类可以参考，org.springframework.security.access.vote.AbstractAccessDecisionManager
//参考 supports 方法，最终会调用默认值前缀验证匹配 `ROLE_`
```

eg: @Secured("ROLE_user")，必须包含 ROLE_ 前缀，多选则 @Secured({"ROLE_user","ROLE_test"})



##### @PreAuthorize() （我常用）

> 在任意配置类上增加 @EnableGlobalMethodSecurity(prePostEnabled = true)
>
> 即可支持 对应`org.springframework.security.access.prepost`下的所有注解

```markdown
只需要注释到所需要校验的资源上即可，另外其支持SpEL表达式，对应spring-security表达式的位置`org.springframework.security.access.expression.SecurityExpressionRoot`

通常使用到的可能就 hasRole()及 hasAuthority()

这里要注意，除非你的角色中本身存在 `ROLE_` 前缀，否则还是使用hasAuthority 替代 hasRole,我目前找不到对应配置能直接改默认前缀，除非重写
```

eg: @PreAuthorize("hasRole('user')") 等同于@PreAuthorize("hasRole('ROLE_user')")  相当于 @PreAuthorize("hasAuthority('ROLE_user')") 



##### @RolesAllowed()

> 在任意配置类上增加@EnableGlobalMethodSecurity(jsr250Enabled = true)
>
> 即可支持对应`javax.annotation.security`下的所有注解
>
> 这里与`@PreAuthorize`一致，会默认增加前缀 `ROLE_`

eg: @RolesAllowed("user") 等于@RolesAllowed("ROLE_user")  ,相当于校验 ROLE_user