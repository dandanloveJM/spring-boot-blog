package hello.configuration;

import com.alibaba.fastjson.JSON;
import hello.entity.UserResult;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {
    @Override
    public void onInvalidSessionDetected(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        System.out.println("没有过期吗");
        UserResult result = UserResult.failure("expire", "expire");

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 设置为0，浏览器端就会删除同域名，同path的cookie
        cookie.setDomain("localhost");
        httpServletResponse.addCookie(cookie);

        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(result));

    }
}
