package com.imooc.aspect;

import com.imooc.constant.CookieConstant;
import com.imooc.constant.RedisConstant;
import com.imooc.exception.SellerAuthorizeException;
import com.imooc.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class SellerAuthorizeAspect {
    @Autowired
    private StringRedisTemplate redisTemplate;


    //切入点是 com.imooc.controller下面Seller开头的类里面的方法 并且排除 com.imooc.controller包下面的SellerUserController类下面的方法
    @Pointcut("execution(public * com.imooc.controller.Seller*.*(..))" +
    "&& !execution(public * com.imooc.controller.SellerUserController.*(..))")
    public  void vertify(){

    }

    @Before("vertify()")
    public void doVertify(){
      ServletRequestAttributes servletRequestAttributes =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = servletRequestAttributes.getRequest();
      //获取cookie
      Cookie cookie =  CookieUtil.get(request, CookieConstant.TOKEN);
      if(cookie == null){
        log.error("[登陆校验] 在cookie中获取到token为空");
        throw new SellerAuthorizeException();
      }
      //去redis中查询
        String tokenValue =redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()));
        if(StringUtils.isEmpty(tokenValue)){
            log.error("[登陆校验] 在redis中获取到token为空");
            throw new SellerAuthorizeException();
        }
    }


}
