# spring-security-review
![springboot](https://img.shields.io/badge/springboot-2.5.0.RELEASE-brightgreen.svg) ![jdk](https://img.shields.io/badge/jdk->=1.8-blue.svg) 

`base dependence`

```xml
    <dependencies>
        <!-- 实现对 Spring MVC 的自动化配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- 实现对 Spring Security 的自动化配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>

```

`baseurl` : localhost:8080

| name |  description     |
| :--: | :--: |
|   [security-demo-1](./security-demo-1)   |   `入门-基于内存配置用户角色`   |
|   [security-demo-2](./security-demo-2)   |    `入门-基于内存接口权限控制`  |
| [security-demo-3](./security-demo-3) | `入门-基于Session一致性问题处理` |
| [security-demo-4](./security-demo-4) | `基于Spring-security 5.X的oauth2样例 （内存模式）（资源/授权分离）` |
| [security-demo-5](./security-demo-5) | `细讲基于WebSecurityConfigurerAdapter整合JWT` |



## <span id="UserDetailsManager">1. UserDetailsManager</span>
> 用户凭证管理配置类：[UserDetailsManagerConfigurer](#UserDetailsManagerConfigurer)

从实现类中可知悉：除了默认的内存管理还有基于jdbc模式的管理方式
`JdbcUserDetailsManagerConfigurer`，`InMemoryUserDetailsManagerConfigurer`



## <span id="accessConfigure">2. 权限配置</span>

### （1）Ant风格表达式（可以传入多个，逗号分割）

>.antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')")
>`最长匹配原则`

| 通配符 |  说明    |
| :--: | :--: |
|   ?    |  匹配任何单字符   |
|   *    |   匹配0或者任意数量的字符   |
|   **   |      匹配0或者更多的目录   |

具体参考类：[SecurityExpressionRoot](#SecurityExpressionRoot)

### （2） antMatchers 后缀

> permitAll （所有用户可访问）
> denyAll （所有用户不可访问）
> authenticated （登录用户可访问） 
> anonymous （无需登录，即匿名用户可访问）
> hasIpAddress （来自指定 IP 表达式的用户可访问）
> ...etc

具体参考类：[ExpressionUrlAuthorizationConfigurer](#ExpressionUrlAuthorizationConfigurer)

### （3）自定义方法鉴权

> 其实我们完全可以自定义一个实体以用于自主资源鉴权 [SpringSecurityConf](./security-demo-5/src/main/java/cn/coffeeandice/config/SpringSecurityConf.java) 
>
> 会自动注入`请求`及`授权信息` ，只需要自行根据url匹配返回布尔值即可
>
> ```
> .access("@rbacauthorityservice.hasPermission(request,authentication)")
> ```

## 3、关于SpringSecurity的验证入口

> 抽象验证校验过滤器`AbstractAuthenticationProcessingFilter`，继承了实现Filter的`GenericFilterBean`
>
> 总体上是对请求内包含的凭据做出响应处理：主要描述在[security-demo-5](./security-demo-5)

###### ![image-20210831232240771](C:\Users\1\Desktop\公考资料\内容图片\image-20210831232240771.png)



```java
/**
*利用Springboot事件分发器初始化内容，触发交互事件使用
**/
protected ApplicationEventPublisher eventPublisher;

protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

private AuthenticationManager authenticationManager;

protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

private RememberMeServices rememberMeServices = new NullRememberMeServices();

private RequestMatcher requiresAuthenticationRequestMatcher;

private boolean continueChainBeforeSuccessfulAuthentication = false;

private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();

private boolean allowSessionCreation = true;

private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
```





# 类全路径目录：

## <span id="UserDetailsManagerConfigurer">1. UserDetailsManagerConfigurer</span>
>`org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer`




## <span id="SecurityExpressionRoot">2. SecurityExpressionRoot</span>
>`org.springframework.security.access.expression.SecurityExpressionRoot`



## <span id="ExpressionUrlAuthorizationConfigurer">3. ExpressionUrlAuthorizationConfigurer</span>
> `org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer`



## <span id="ExpressionUrlAuthorizationConfigurer">4. AbstractAuthenticationProcessingFilter</span>

>`org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter`
