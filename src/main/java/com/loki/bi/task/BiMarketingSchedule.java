package com.loki.bi.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.home.config.task
 * @Description: TODO
 * @date Date : 2023 年 06月 12 日 18:57
 */

@Component
@Slf4j
public class BiMarketingSchedule {

    @Scheduled(fixedDelay = 1000 * 60 * 5) //每隔三秒钟执行一次心跳
    public void heathy() {
        log.info("heart peng...peng..");
    }
}
