# 	基于内存的基础 jwt 样例 
 [返回主文档](../README.md)



```xml
<!-- 用于免去自己封装处理jwt -->
<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.78</version>
</dependency>
```



## 一、什么是JWT（JSON WEB TOKEN）

> 适用于分布式站点传递信息的一个标准(本身不保证安全)【简单对称算法】
>
> 整体来说就就是将`标识部分`和`信息内容部分`进行加密处理，以最后一部分进行加密验证

**通常现在整体结构分为三个部分**：header、playload、signature,以圆点`.`组合拼接构成

### 1、header（头部信息）

> 对头部内容进行base64编码

```json
{
  'typ': 'JWT', //这个基本不需要变化，用于统一标准辨识，当然有个人需要也可以改变
  'alg': 'HS256' //举例子都用默认的，当然这里可以自定义算法，用于自辨析
}
```

### 2、playload（信息载体）

> 对内容进行base64编码【尽量不要将涉密信息写在里面】

```json
{
  "expire": "1234567890", //设置过期时间是为了更好使用令牌
  "name": "Ringo",
}
```

### 3、signature（签名验证）

> 这部分开始组装，将1、2部分的内容以圆点`.`连接，设为`X`
>
> 将X以声明的方式`HS256`(上方假定的加密方式)加密 `X`与自定义盐（自定义的salt）组合的内容，形成第三部分

```javascript
// 伪代码如下
let X = base64UrlEncode(header) + '.' + base64UrlEncode(payload);

let signature = HMACSHA256(X, 'secret');// secret指代自定义加盐
```



最后jwt为：

```
X+‘.’+signature ==> jwt
```



## 二、利用好适配器WebSecurityConfigurerAdapter
> 其实基本所有配置都基于这个适配器，慢慢去排查还是很多可以自定义的

### 1、安全认证（自定义认证）

> 这里通常是用于校验用户权限的，可以自定义方法去处理请求认证的用户
>
> 只需要定义
>
> 1、基于`UserDetailsService`实现的方法
>
> 2、实现`UserDetails`基类的用户信息类即可



## 三、Security 鉴权流程

>触发前提：配置一个配置类继承`WebSecurityConfigurerAdapter`
>
>`HttpSecurity`所在config内，最小配置
>
>```java
> http.csrf().disable()
>                .authorizeRequests()
>                .anyRequest()
>                .authenticated()
>                
>                .and()
>                .formLogin(); //若没有这个配置则不会触发，同时会直接触发空身份验证 `Http403ForbiddenEntryPoint`处理
>```



### 1、经过自定义jwt过滤器

> [JwtAuthenticationTokenFilter](./src/main/java/cn/coffeeandice/config/auth/JwtAuthenticationTokenFilter.java) 主要是为了处理凭证，若通过验证，则将凭证存放到Security全局上下文中 



### 2、验证入口处理过滤器

> 进入`AbstractAuthenticationProcessingFilter`

这里经过两个流程

#### ①判断是否是不需要验证的过滤器

> `requiresAuthentication`

  例如登陆页面和退出页面，通常我们需要忽略掉，则会跳出这个过滤器

```java
/**
*标准情况下配HttpSecurity内则会跳过 /login、 /logout的判断流程
**/
.and()
.formLogin()  //开启登录
.permitAll()
.and()
.logout()
.permitAll();
```

#### ②尝试判断凭证流程

> `attemptAuthentication`

1、校验判断请求是否满足post请求，否则返回不支持异常

2、将请求附带的帐号密码讯息填充到`request`请求中以便后续校验

3、下列两种方式都有定义`userDetailsService`并且配置【实现UserDetails的身份鉴权方法】，这里鉴权是为了给当前会话中加入SecurityContext身份信息

#### 鉴权方式：

##### 默认端点鉴权:

> 也就是直接通过注解标签的形式判断权限，如 `.access("hasRole(admin)")`，会读取对应端点的注解来判断是否足够权限往下执行，可以参考 [权限配置](../README.md?accessConfigure)





##### 自定义端点鉴权：

> 参考[RbacAuthorityService](./src/main/java/cn/coffeeandice/service/RbacAuthorityService.java)，只需要定义对基于`PathMatcher`能匹配到的url，返回布尔值即可判断端点是否授权。
>
> Tips: 默认login/logout的端点是开放的
>
> 这里考虑了上述[JwtAuthenticationTokenFilter](./src/main/java/cn/coffeeandice/config/auth/JwtAuthenticationTokenFilter.java) 过滤器对SpringSecurit的Content的设置前提
>
> 这里除了常规的身份鉴权外，还需要进入自定义鉴权来判断url是否拥有资源权限



#### 鉴权流程：（开启表单登陆）

> 若未开启表单登陆则会直接判断：
> **无权限**： `AuthenticationEntryPoint`处理
>
> **有权限**：直接进入端点

#### （1）若符合通过凭证验证

​		触发发布通过校验事件： [AjaxAuthenticationSuccessHandler](./src/main/java/cn/coffeeandice/config/auth/AjaxAuthenticationSuccessHandler.java) 
```markdown
若未实现`AuthenticationSuccessHandler`,并且将其设置到`successHandler`内初始化，
则会进入默认的：SavedRequestAwareAuthenticationSuccessHandler 处理
```



#### （2）若不符合通过凭证验证（若是登陆页面）

​		触发发布校验失败事件： [AjaxAuthenticationFailureHandler](./src/main/java/cn/coffeeandice/config/auth/AjaxAuthenticationFailureHandler.java)

```markdown
若未实现`AuthenticationFailureHandler`，并且将其设置到`failureHandler`内初始化
则会进入默认的：SimpleUrlAuthenticationFailureHandler处理进程，同时跳转默认login?erro页面
```

#### （2）若不符合通过凭证验证（非登陆页面）

​      触发默认委派任务： `LoginUrlAuthenticationEntryPoint`

```markdown
重新跳转至登陆界面，若重新实现了`AuthenticationEntryPoint`接口并且在配置类中注册，则走自定义配置
```

​		

### 3、消除权限（LogOut）

> 这里基于`sessionManagement`策略又额外分为几种处理逻辑

#### ①`LogoutFilter`,判断是否属于logout端点

>默认不修改时都是logout

#### ②`CompositeLogoutHandler`处理所有处理类

#### ③执行自定义实现`LogoutSuccessHandler`处理类



# 相关全路径地址：

### <span id="Http403ForbiddenEntryPoint">1. Http403ForbiddenEntryPoint</span>
>`org.springframework.security.web.authentication.Http403ForbiddenEntryPoint`



### <span id="Http403ForbiddenEntryPoint">2. AbstractAuthenticationProcessingFilter</span>

>`org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter`



### 3、LogoutFilter

> org.springframework.security.web.authentication.logout.LogoutFilter
