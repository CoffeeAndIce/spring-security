package cn.coffeeandice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

/**
 * @author : CoffeeAndIce
 * @Todo: 删除令牌示例
 * @Date: 2021/07/17
 */
@RestController
public class TokenController {
    //等待自动注入即可
    @Autowired
    private InMemoryTokenStore tokenStore;

    @PostMapping("/revoke/token")
    public boolean revokeToken(@RequestParam("token") String tokenValue) {
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

}
