package com.loki.bi.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.util
 * @Description: TODO
 * @date Date : 2023 年 10月 06 日 2:46
 */
public class RestTemplateUtil {

    /**
     * HttpEntity 封装,添加 cookie
     *
     * @return
     */
    public static HttpEntity createBiliHttpEntityWithCookie() {
        return createHttpEntity(HttpHeaders.COOKIE, BiliCookieConfig.cookie);
    }

    /**
     * HttpEntity 封装,添加 cookie
     *
     * @return
     */
    public static HttpEntity createHttpEntity(String key,String value) {
        HttpHeaders head = createHttpEntityHeader();
        head.set(key, value);
        return new HttpEntity(head);
    }

    /**
     * HttpEntity 封装,添加 cookie
     *
     * @return
     */
    public static HttpEntity createHttpEntity() {
        HttpHeaders head = createHttpEntityHeader();
        return new HttpEntity(head);
    }


    public static HttpEntity createNewRankHttpEntity(Map<String,String> argsMap){
        HttpHeaders head = createHttpEntityHeader();
        head.set(HttpHeaders.COOKIE, NewRankCookieConfig.cookie);
        head.set(NewRankCookieConfig.N_TOKEN, NewRankCookieConfig.N_TOKEN_VALUE);
        return new HttpEntity(argsMap, head);
    }


    public static HttpHeaders createHttpEntityHeader() {
        HttpHeaders head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);
        head.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        head.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
        return head;
    }
}
