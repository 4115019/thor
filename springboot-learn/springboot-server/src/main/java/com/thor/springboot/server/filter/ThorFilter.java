package com.thor.springboot.server.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author huangpin
 * @date 2018-12-20
 */
@WebFilter(filterName = "ThorFilter", urlPatterns = "*")
public class ThorFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("--------------------Filter过滤:start----------------------");
        filterChain.doFilter(request, response);
        System.out.println("--------------------Filter过滤:end----------------------");
    }

    @Override
    public void destroy() {

    }
}
