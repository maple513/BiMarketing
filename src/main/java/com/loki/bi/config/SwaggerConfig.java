package com.loki.bi.config;


import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.home.config
 * @Description: Swagger配置中心
 * @date Date : 2023 年 06月 13 日 1:13
 */

@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    String swagger;

//    @Bean
//    public Docket homeSwaggerConfig() {

//        return new Docket(DocumentationType.OAS_30)
//                .groupName("home")
//                .apiInfo(ApiInfo.DEFAULT)
//                .enable(swagger.equals("PROD")?false:true)
//                .select()
//                .paths(PathSelectors.regex("/home/.*"))
//                .build();
//    }

    // 拦截器排除swagger设置, 忽略路径
    // interceptorRegistration
    // .excludePathPatterns("/swagger**/**")
    // .excludePathPatterns("/webjars/**")
    // .excludePathPatterns("/v3/**")
    // .excludePathPatterns("/doc.html")

    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi() {
                log.info("swagger switch : [{}]",swagger.equals("PROD")?false:true);
        return new Docket(DocumentationType.OAS_30)
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo())
                .enable(swagger.toUpperCase().equals("PROD")?false:true)
                // 设置哪些接口暴露给Swagger展示
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("HomeAndStock：API接口文档")
                .description("describle：Service backend test ")
                .contact(new Contact("loki", "https://github.com/maple513", "13113627831@163.com"))
                .version("version:1.0.0")
                .build();
    }
}
