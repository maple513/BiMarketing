package com.loki.bi.Filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class BiMarketingFilter implements Filter {

    public void init() throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Bimarketing Filter 生效..");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
