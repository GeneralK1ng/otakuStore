package com.otaku.interceptor;

import com.otaku.constant.JwtClaimsConstant;
import com.otaku.context.BaseContext;
import com.otaku.properties.JwtProperties;
import com.otaku.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 如果请求的是注册接口，不进行JWT令牌验证
        if (isRegistrationEndpoint(request)) {
            return true;
        }

        log.info("当前请求路径：{}", request.getRequestURI());

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());


        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    private boolean isRegistrationEndpoint(HttpServletRequest request) {
        // 根据URL路径规则判断是否是注册接口
        // 如果注册接口的路径是 "/user/user/register"，可以使用如下判断：
        return request.getRequestURI().equals("/user/user/register") && request.getMethod().equals("POST");
    }
}
