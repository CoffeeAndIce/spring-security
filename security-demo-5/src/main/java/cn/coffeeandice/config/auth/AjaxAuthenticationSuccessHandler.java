package cn.coffeeandice.config.auth;

import cn.coffeeandice.entity.CustomerUserDetails;
import cn.coffeeandice.entity.response.ResultResponse;
import cn.coffeeandice.utils.JwtTokenUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author : CoffeeAndIce
 * @Todo: 通常为尝试登陆成功触发
 * @Date: 2021/07/27
 */

@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        ResultResponse responseBody = new ResultResponse();

        responseBody.setStatus("200");
        responseBody.setMsg("Login Success!");

        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();

        String jwtToken = JwtTokenUtil.generateToken(userDetails.getUsername(), 300, "_secret");
        responseBody.setJwtToken(jwtToken);

        httpServletResponse.getWriter().write(JSON.toJSONString(responseBody));
    }


}
