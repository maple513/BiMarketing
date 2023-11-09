package com.loki.bi.Filter;

import com.loki.bi.Filter.BiMarketingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiMarktingFilterConfig {

    @Bean
    public FilterRegistrationBean registerFilter() {
        FilterRegistrationBean register = new FilterRegistrationBean();
        register.setFilter(new BiMarketingFilter());
        register.addUrlPatterns("/*");
        register.setName("BiMarktingFilter");
        register.setOrder(1);
        return register;
    }

    //也可以通过webFilter 注解效果一致 @WebFilter(urlPatterns = "/*", filterName = "MapleFilter")
}
