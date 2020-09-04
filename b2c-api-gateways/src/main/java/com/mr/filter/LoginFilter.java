package com.mr.filter;


import com.mr.common.utils.CookieUtils;
import com.mr.config.FilterProperties;
import com.mr.config.JwtConfig;
import com.mr.util.JwUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtConfig.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取请求的url
        String requestURI = req.getRequestURI();
        //当前请求如果不再白名单内则开启拦截器
        return !isAllowPath(requestURI);
    }

    @Autowired
    private FilterProperties filterProp;
    //传入url判断是否是在白名单内
    private boolean isAllowPath(String requestURI) {
        boolean flag = false;
        // 循环白名单判断
        for (String path : this.filterProp.getAllowPaths()) {
            // 如果存在于白名单，立即返回true，结束循环
            if(requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        System.out.println("拦截到请求"+request.getRequestURI());
        // 获取token
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        System.out.println("token信息"+token);

        // 校验
        try {
            // 通过公钥解密，如果成功，就放行，失败就拦截
            JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            System.out.println("解析失败 拦截"+token);
            // 校验出现异常，返回403
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}