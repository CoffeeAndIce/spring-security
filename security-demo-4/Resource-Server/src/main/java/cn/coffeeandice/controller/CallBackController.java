package cn.coffeeandice.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author : CoffeeAndIce
 * @Todo: 响应回调地址使用的地址
 * @Date: 2021/07/16
 */

@RestController
@RequestMapping("/test")
public class CallBackController {
    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;

    @Value("${security.oauth2.access-token-uri}")
    private String accessTokenUri;

    /**
     * @param code 授权码值 ,授权码模式使用
     * @Todo 适应回调地址直接返回通行令牌
     */
    @GetMapping("/callback")
    public OAuth2AccessToken accessTokenCallBack(@RequestParam("code") String code,
                                                 HttpServletRequest request) {
        // (A) 创建 AuthorizationCodeResourceDetails 对象
        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setClientId(oauth2ClientProperties.getClientId());
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        // (B)创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        //      (a)设置 code
        restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setAuthorizationCode(code);
        //      (b) 通过这个方式，设置 redirect_uri 参数
        restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setPreservedState(request.getRequestURL().toString());
        restTemplate.setAccessTokenProvider(new AuthorizationCodeAccessTokenProvider());
        // (C)获取访问令牌
        return restTemplate.getAccessToken();
    }


    /**
     * @Todo 适应回调地址,简化模式测试
     */
    @GetMapping("/implicit")
    public OAuth2AccessToken implicitCallBack(HttpServletRequest request){
        return null;
    }
}
