package cn.coffeeandice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author : CoffeeAndIce
 * @Todo: 授权服务器搭建
 * @Date: 2021/07/11
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;

    /**
     * 用户认证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
        endpoints.tokenStore(tokenStore);
//        endpoints.userDetailsService(userDetailsService);
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
                //          (b-1)授权模式类型支持
                // .authorizedGrantTypes("authorization_code")
                //          (b-2)回调地址配置
                //          额外url[/callback]支持在资源服务内 cn.coffeeandice.controller.CallBackController
                //          同时开启免校验 cn.coffeeandice.config.OAuth2ResourceServerConfig
                //.redirectUris("http://localhost:9090/test/callback")
                //      （c)简化模式
                // .authorizedGrantTypes("implicit")
                //.redirectUris("http://localhost:9090/test/implicit")
                //      (d)客户端模式 客户端模式登陆支持在资源服务内 cn.coffeeandice.controller.LoginController
                // .authorizedGrantTypes("client_credentials")
                //      (e)刷新令牌支持，不建议使用,需要定义一个UserDetailsService
                // .authorizedGrantTypes("refresh_token")
                .redirectUris("http://localhost:9090/test/callback", "http://localhost:9090/test/implicit")
                // 开启全部模式
                .authorizedGrantTypes("password", "authorization_code", "implicit","client_credentials","refresh_token")
                // (C) 可以选择授权的范围,scopes内容是自定义描述的
                .scopes("read_userinfo", "read_contacts")
                // (D）可以继续配置其他Client 账户
                //.and().withClient()
        ;
    }


}

