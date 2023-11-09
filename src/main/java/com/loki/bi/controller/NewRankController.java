package com.loki.bi.controller;

import com.loki.bi.service.NewRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.controller
 * @Description: TODO
 * @date Date : 2023 年 10月 04 日 20:11
 */
@Slf4j
@RestController
@RequestMapping(path = "/newRank")
public class NewRankController {

    @Autowired
    NewRankService newRankService;

    /**
     * 获取b站每周热度最高的视频信息
     *
     * @return
     */
    @RequestMapping(path = "/hotByWeek", method = RequestMethod.GET)
    public ResponseEntity<String> biliHotByWeek() {
        log.info("bilibili hot vedios of week");
        String block = "1";//生活板块
        //获取生活区的热点视屏信息
        newRankService.biliHotByWeek(null,NewRankService.CYCLE_TYPE.week, block);
        return ResponseEntity.ok("done");
    }

    /**
     * 获取b站所有板块每周热度最高的视频数据
     *
     * @return
     */
    @RequestMapping(path = "/hotAllByWeek", method = RequestMethod.GET)
    public ResponseEntity<String> biliHotAllByWeek() {
        log.info("bilibili hot vedios of week");
        newRankService.biliHotAll(NewRankService.CYCLE_TYPE.week);
        return ResponseEntity.ok("done");
    }

    /**
     * 获取b站所有板块每日热度最高的视频数据
     *
     * @return
     */
    @RequestMapping(path = "/hotAllByDay", method = RequestMethod.GET)
    public ResponseEntity<String> biliHotAllByDay() {
        log.info("bilibili hot vedios of day");
        newRankService.biliHotAll(NewRankService.CYCLE_TYPE.day);
        return ResponseEntity.ok("done");
    }
}
