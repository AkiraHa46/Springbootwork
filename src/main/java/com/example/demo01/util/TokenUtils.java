package com.example.demo01.util;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo01.entity.User;
import com.example.demo01.service.UserService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

//@Component
public class TokenUtils {

    private static UserService staticUserService;

    @Resource
    private UserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成token
     * @return
     */
    public static String getToken(String userId, String sign) {
        return JWT.create().withAudience(userId) //将userId保存到token里面作为载荷
        .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) //2小时后token过期
        .sign(Algorithm.HMAC256(sign)); //以sign作为token密钥
    }

    /**
     * 获取当前用户的登录信息
     * @return user对象
     */
    public static User getCurrentUser() {

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            }
        }catch (Exception e) {
                return null;
        }
        return null;
    }
}
