package cn.coffeeandice.entity.response;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : CoffeeAndIce
 * @Todo: 不符合端点或登录
 * @Date: 2021/07/27
 */
@Component
public class AsyAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResultResponse responseBody = new ResultResponse();
        responseBody.setStatus("000");
        responseBody.setMsg("Need Authorities!");
        httpServletResponse.getWriter().write(JSON.toJSONString(responseBody));
    }
}
