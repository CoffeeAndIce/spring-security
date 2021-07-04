### spring-security-review
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



###### <span id="UserDetailsManager">1. UserDetailsManager</span>
> 用户凭证管理配置类：[UserDetailsManagerConfigurer](#UserDetailsManagerConfigurer)

从实现类中可知悉：除了默认的内存管理还有基于jdbc模式的管理方式
`JdbcUserDetailsManagerConfigurer`，`InMemoryUserDetailsManagerConfigurer`



###### <span id="accessConfigure">2. 权限配置</span>

1、Ant风格表达式（可以传入多个，逗号分割）
>.antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')")
>`最长匹配原则`

| 通配符 |  说明    |
| :--: | :--: |
|   ?    |  匹配任何单字符   |
|   *    |   匹配0或者任意数量的字符   |
|   **   |      匹配0或者更多的目录   |

具体参考类：[SecurityExpressionRoot](#SecurityExpressionRoot)

2、 antMatchers 后缀
> permitAll （所有用户可访问）
> denyAll （所有用户不可访问）
> authenticated （登录用户可访问） 
> anonymous （无需登录，即匿名用户可访问）
> hasIpAddress （来自指定 IP 表达式的用户可访问）
> ...etc

具体参考类：[ExpressionUrlAuthorizationConfigurer](#ExpressionUrlAuthorizationConfigurer)









### 类全路径目录：

###### <span id="UserDetailsManagerConfigurer">1. UserDetailsManagerConfigurer</span>
>`org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer`




###### <span id="SecurityExpressionRoot">2. SecurityExpressionRoot</span>
>`org.springframework.security.access.expression.SecurityExpressionRoot`



###### <span id="ExpressionUrlAuthorizationConfigurer">3. ExpressionUrlAuthorizationConfigurer</span>
`org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer`
