# 基于Spring-security 5.X的oauth2样例 （内存模式）
 [返回主文档](../README.md)

> 基于demo-2为基础改造
>
> 按照官方文档而言，5.x 后续SSO功能都会重新更新，但目前而言还是可以使用



## 一、pom.xml 完整依赖

> 下面`spring-security-oauth2`所选版本为了贴合当前选用springboot而选择，具体依赖可以参考[其中链接](https://mvnrepository.com/artifact/org.springframework.security.oauth/spring-security-oauth2/2.5.0.RELEASE})

`base dependence`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- 支持oauth2配置的最终版本，内部已经包含了security -->
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.3.12.RELEASE</version>
</dependency>
```



## 二、配置认证管理器

> 在原有配置文件上初始化加载认证管理器，为授权服务器做铺垫
>
> 实际上也是通过 **Spring Security**来做授权认证
>

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	...
    /**
     * @Todo 配置用户认证管理器
     */
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    ...
```



## 三、配置授权服务器

> （Endpoint）端点中：`/oauth/check_token`不是账户密码校验，是针对资源服务器中的`client-id` 与` client-secret`认证
>
> `通常根据业务大小决定是否根据模式拆分授权服务器增加并发性`

```java
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 用户认证管理器(上一步的铺垫动作)
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        // 主要是针对 `/oauth/check_token`这个端点要认证才能访问
        // 这里的认证不是账户密码哈，是针对资源中的 client-id 与 client-secret认证
        oauthServer.checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //（A）Client 账号、密码，相当于 client-id 与 client-secret
                .withClient("clientapp").secret("123456")
                //（B）授权模式
                //      (a)密码模式
                // .authorizedGrantTypes("password")
                //      (b)授权模式
                //      (b-1)授权模式类型支持
                // .authorizedGrantTypes("authorization_code")
                //      (b-2)授权模式类型支持,额外支持在资源服务内 cn.coffeeandice.controller.CallBackController
                .redirectUris("http://localhost:9090/callback","https://www.baidu.com")
                .authorizedGrantTypes("password","authorization_code")
                // (C) 可以选择授权的范围,scopes内容是自定义描述的
                .scopes("read_userinfo", "read_contacts")
                 // (D）可以继续配置其他Client 账户
                 //.and().withClient()
        ;
    }

}
```



### 1、授权模式拓展

> 上述 B 步骤设置实际上是允许的模式设置一般来说可以为多个值，也就是允许多种模式
>
> `多个值情况直接逗号分割即可` 
>
> eg: .authorizedGrantTypes("password","authorization_code")

|        值名称        |    描述    |
| :------------------: | :--------: |
|      `password`      |  密码模式  |
| `authorization_code` | 授权码模式 |
|       `token`        |  简化模式  |
| `client_credentials` | 客户端模式 |



## 四、测试

> 建议使用postMan做测试 （本环节内容都以postMan作为基础工具）
>
> {{base_oauth}} == `localhost:8080/oauth`

### 1、密码模式（password）

> POST — {{base_oauth}}/token
>
> 
>
> 简单粗暴，直接获取Access_token



`Authorization：Basic Auth `

> 密码模式的basic验证以客户端kv来做校验

|   键名   |   键值    |       描述       | 必填 |
| :------: | :-------: | :--------------: | :--: |
| username | clientapp |   clientId的值   |  是  |
| password |  123456   | clientSecret的值 |  是  |

`Body: form-data`

|    键名    |            键值             |                        描述                        | 必填 |
| :--------: | :-------------------------: | :------------------------------------------------: | :--: |
| grant_type |          password           |                      授权类型                      |  是  |
|  username  |            admin            |                       用户名                       |  是  |
|  password  |            admin            |                      用户密码                      |  是  |
|   scope    | read_userinfo read_contacts | 权限域值，为空则默认获取全部权限，多个时以空格分割 |  否  |

`响应值: Response`

```json
{
    "access_token": "6pT3fMLyo6+784Z0nJtBD5tcXyY=",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read_userinfo read_contacts"
}
```



### 2、校验令牌

> POST  — {{base_oauth}}/check_token

`Authorization：Basic Auth `

|   键名   |   键值    |       描述       | 必填 |
| :------: | :-------: | :--------------: | :--: |
| username | clientapp |   clientId的值   |  是  |
| password |  123456   | clientSecret的值 |  是  |

`Body: form-data`

| 键名  |             键值             |        描述        | 必填 |
| :---: | :--------------------------: | :----------------: | :--: |
| token | 6pT3fMLyo6+784Z0nJtBD5tcXyY= | 获取的access_token |  是  |

`响应值: Response`

```json
{
    "active": true,
    "exp": 1626062730,
    "user_name": "admin",
    "authorities": [
        "ROLE_ADMIN"
    ],
    "client_id": "clientapp",
    "scope": [
        "read_userinfo",
        "read_contacts"
    ]
}
```



### 3、授权码模式（authorization_code）

> GET— {{base_oauth}}/authorize
>
> 
>
> access_token 只能通过 `authorization_code`的方式获取真正的access_token
>
> 简单划分可以是：①获取授权码  ②根据授权码去获取真正的access_token
>
> 整体会校验的是中间的`授权码`与`重定向url`

#### （1）第一步，授权属性

`basic request`

> http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:9090/test/implicit&response_type=token&scope=read_userinfo&state=666

|     键名      |                键值                 |                        描述                        | 必填 |
| :-----------: | :---------------------------------: | :------------------------------------------------: | :--: |
|   client_id   |              clientapp              |                    clientId的值                    |  是  |
| redirect_uri  | http://localhost:9090/test/implicit |     重定向的url（测试可以任意无需授权的地址）      |  是  |
| response_type |                code                 |                 必填`token`,不能改                 |  是  |
|     scope     |     read_userinfo read_contacts     | 权限域值，为空则默认获取全部权限，多个时以空格分割 |  否  |
|     state     |                 666                 |       可以填写任意值，会完整返回附带到地址上       |  否  |

#### （2）第二步，选择授权域

> `一般来说，如果回调地址需要校验其他权限，会先执行才到本步骤，这里默认回调地址不需要任意鉴权或已经有权限进入`
>
> 通常会走最丑原生页面，上面会出现你scope中配置的值，勾选即可



#### （3）第三步，跳转回调地址

`响应值: Response`

> 会跳转值设定的url回调地址上，其中`code`为用于申请 `access_token`的所谓授权码

```
http://localhost:9090/callback?code=i_zySK&state=666
```



#### （4）第四步，获取访问令牌

> POST — {{base_oauth}}/token
>
> 
>
> `当前步骤无论输错什么，授权码都会失效，只能使用一次` ,否则是抛出`InvalidGrantException` 异常信息，可以增加拦截器处理

`Authorization：Basic Auth `

|   键名   |   键值    |       描述       | 必填 |
| :------: | :-------: | :--------------: | :--: |
| username | clientapp |   clientId的值   |  是  |
| password |  123456   | clientSecret的值 |  是  |

`Body: form-data`

|     键名     |              键值              |                     描述                     | 必填 |
| :----------: | :----------------------------: | :------------------------------------------: | :--: |
|  grant_type  |       authorization_code       |           必填`authorization_code`           |  是  |
| redirect_uri | http://localhost:9090/callback | 测试可以任意无需授权的地址，与授权时必须一致 |  是  |
|     code     |             i_zySK             |         通过前三步获取的一个授权码值         |  是  |

`响应值: Response`

```json
{
    "access_token": "6ilJ9XCa1U2+eJB0Fd4MeT7zxOA=",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read_userinfo read_contacts"
}
```



### 4、简化模式(implicit)

> GET— {{base_oauth}}/authorize
>
> 
>
> 就是简化了要利用`授权码`去获取`访问令牌`的过程,但同时也增加了风险，因为所有值都展示在url上
>
> Tips: 要自定义页面处理，实际上使用率不高



#### （1）第一步，授权属性

`basic request`

> http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:9090/callback&response_type=code&scope=read_userinfo&state=666

|     键名      |              键值              |                        描述                        | 必填 |
| :-----------: | :----------------------------: | :------------------------------------------------: | :--: |
|   client_id   |           clientapp            |                    clientId的值                    |  是  |
| redirect_uri  | http://localhost:9090/callback |     重定向的url（测试可以任意无需授权的地址）      |  是  |
| response_type |              code              |                 必填`code`,不能改                  |  是  |
|     scope     |  read_userinfo read_contacts   | 权限域值，为空则默认获取全部权限，多个时以空格分割 |  否  |
|     state     |              666               |       可以填写任意值，会完整返回附带到地址上       |  否  |



#### （2）第二步，选择授权域

> `一般来说，如果回调地址需要校验其他权限，会先执行才到本步骤，这里默认回调地址不需要任意鉴权或已经有权限进入`
>
> 通常会走最丑原生页面，上面会出现你scope中配置的值，勾选即可



#### （3）第四步，获取访问令牌

> url 中直观看到数值

```
http://localhost:9090/test/implicit#access_token=dWB3GuRgQvQLcHtTft7OY0M/JHg=&token_type=bearer&state=666&expires_in=43199
```



### 5、客户端模式(client_credentials)

> POST— {{base_oauth}}/token
>
> 
>
> 这种应该属于最熟悉的一种了，直接利用客户端密钥获取`访问令牌`

`Authorization：Basic Auth `

|   键名   |   键值    |       描述       | 必填 |
| :------: | :-------: | :--------------: | :--: |
| username | clientapp |   clientId的值   |  是  |
| password |  123456   | clientSecret的值 |  是  |

`Body: form-data`

|    键名    |        键值        |           描述           | 必填 |
| :--------: | :----------------: | :----------------------: | :--: |
| grant_type | client_credentials | 必填`client_credentials` |  是  |

响应值: Response

```json
{
    "access_token": "z07dSAgllkplbDWrFBKtW7ahFLg=",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read_userinfo read_contacts"
}
```





### 6、刷新令牌

> 允许对通行令牌进行刷新（不建议使用）

> POST— {{base_oauth}}/token
>
> 
>
> 这种应该属于最熟悉的一种了，直接利用客户端密钥获取`访问令牌`

`Authorization：Basic Auth `

|   键名   |   键值    |       描述       | 必填 |
| :------: | :-------: | :--------------: | :--: |
| username | clientapp |   clientId的值   |  是  |
| password |  123456   | clientSecret的值 |  是  |

`Body: form-data`

|     键名      |               键值                |            描述             | 必填 |
| :-----------: | :-------------------------------: | :-------------------------: | :--: |
|  grant_type   |           refresh_token           |     必填`refresh_token`     |  是  |
| refresh_token | z554435AgllkplbDWrFBKtW7ahF434Lg= | 返回值中的`refresh_token`值 |  是  |

响应值: Response

```json
{
    "access_token": "z07dSAgllkplbDWrFBKtW7ahFLg=",
    "token_type": "bearer",
    "refresh_token": "z554435AgllkplbDWrFBKtW7ahF434Lg=",
    "expires_in": 43199,
    "scope": "read_userinfo read_contacts"
}
```

#### （1）配置`UserDetailsService`

> 否则触发异常 `java.lang.IllegalStateException: UserDetailsService is required.`

 资源服务器中

cn.coffeeandice.config.SecurityConfig 注释掉的 `userDetailsServiceBean` 值

填充到 cn.coffeeandice.config.OAuth2AuthorizationServerConfig 内端点配置内即可

> ```
>  endpoints.userDetailsService(userDetailsService);
> ```



### 7、删除令牌

> 既然会过期就必然会存在删除令牌，所以抄作业地址`org.springframework.security.oauth2.provider.token.DefaultTokenServices#revokeToken`

```java
//等待自动注入即可
@Autowired
private ConsumerTokenServices tokenServices;
public boolean revokeToken(String tokenValue) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
		if (accessToken == null) {
			return false;
		}
		if (accessToken.getRefreshToken() != null) {
			tokenStore.removeRefreshToken(accessToken.getRefreshToken());
		}
		tokenStore.removeAccessToken(accessToken);
		return true;
	}
```



## 五、配置资源服务器

> 整个资源管理器其实也是一个客户端把，可以想象成如下结构（四步流程）
>
>    Authorization Server (负责校验授权)
>
> ​    	    ^						     ^                |  ④
>
> ​			| ①						| ③		   V
>
> ​		client   —②—> Resources Server （相当于利用拦截过滤的方式校验权限是否拥有访问资源的授权）



### 1、资源拦截配置

```java
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 设置 /login 无需权限访问
                .antMatchers("/login").permitAll()
                // 设置其它请求，需要认证后访问
                .anyRequest().authenticated()
        ;
    }
}
```



### 2、资源服务配置

#### （A）yaml模式

```yaml
server:
  port: 9090

security:
  oauth2:
    # OAuth2 Client 配置，对应 OAuth2ClientProperties 类
    # org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties
    client:
      client-id: clientapp
      client-secret: 123456
    # OAuth2 Resource 配置，对应 ResourceServerProperties 类
    #org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
    resource:
      token-info-uri: http://127.0.0.1:8080/oauth/check_token # 获得 Token 信息的 URL
    # 访问令牌获取 URL，自定义的
    access-token-uri: http://127.0.0.1:8080/oauth/token

```



### 3、测试资源权限接口

> 主要是测试资源服务器是否与授权服务器接通

> base_resource = localhost:9090
>
> Get — {{base_oauth}}/api/hi

`Authorization：Basic Auth `

| 键名  |             键值             |        描述        |
| :---: | :--------------------------: | :----------------: |
| token | 6pT3fMLyo6+784Z0nJtBD5tcXyY= | 获取的access_token |

`响应值: Response`

```json
world
```



### 4、登陆接口编写

这里需要引入资源服务器该有的校验了；

> **OAuth2ClientProperties**: 说白了就是用来读取yaml上参数用的
>
> **accessTokenUri**：就是之前自定义用于获取token的
>
> **ResourceOwnerPasswordResourceDetails**：整体上可以当做我们获取token的一个包装类，你可以查看代码就知道结构
>
> **OAuth2RestTemplate**：针对Oauth2的一个封装请求类（默认是获取所有scope）
>
> ​				|  （AccessTokenProvider抽象接口，实现类分别代表五种授权类型）
>
> ```java
> AuthorizationCodeAccessTokenProvider：授权码模式
>     
> ClientCredentialsAccessTokenProvider：客户端模式
>     
> ImplicitAccessTokenProvider：简化模式
>     
> ResourceOwnerPasswordAccessTokenProvider：密码模式
> ```



> 如下BC部分可以用客户端自行实现，A部分有现成封装，基本可以继续使用

```java
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;

    @Value("${security.oauth2.access-token-uri}")
    private String accessTokenUri;

    @PostMapping("/password-login")
    public OAuth2AccessToken login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
        // <A> 创建 ResourceOwnerPasswordResourceDetails 对象
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setClientId(oauth2ClientProperties.getClientId());
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);
        // <B> 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
        // <C> 获取访问令牌
        return restTemplate.getAccessToken();
    }

    @PostMapping("/client-login")
    public OAuth2AccessToken login() {
        //(A) 创建 ClientCredentialsResourceDetails 对象
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setClientId(oauth2ClientProperties.getClientId());
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        //(B) 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ClientCredentialsAccessTokenProvider());
        //(C) 获取访问令牌
        return restTemplate.getAccessToken();
    }
}
```

