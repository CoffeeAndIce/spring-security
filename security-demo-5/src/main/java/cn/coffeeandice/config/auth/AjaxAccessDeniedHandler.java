package cn.coffeeandice.config.auth;

import cn.coffeeandice.entity.response.ResultResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : CoffeeAndIce
 * @Todo: 授权失败后处理：通常有两种，一种是默认情况下没权限，另一个是端点权限不足会触发
 * @Date: 2021/07/27
 */

@Component
public class AjaxAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResultResponse responseBody = new ResultResponse();

        responseBody.setStatus("300");
        responseBody.setMsg("need to Authorities!");
        httpServletResponse.getWriter().write(JSON.toJSONString(responseBody));
    }
}
