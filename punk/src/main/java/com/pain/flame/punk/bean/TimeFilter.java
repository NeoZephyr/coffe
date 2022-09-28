package com.pain.flame.punk.bean;

import com.pain.flame.punk.exception.NotAllowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
public class TimeFilter implements Filter {


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public TimeFilter() {
        System.out.println("");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader("token");

        if ("hello".equals(token)) {
            resolver.resolveException(httpServletRequest, httpServletResponse, null, new NotAllowException());
            return;
        }

        chain.doFilter(request, response);
        long end = System.currentTimeMillis();
        System.out.println("执行时间(ms):" + (end - start));
    }
}
