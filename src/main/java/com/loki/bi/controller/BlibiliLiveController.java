package com.loki.bi.controller;

import com.loki.bi.service.BilibiliLiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(path = "/live")
public class BlibiliLiveController {

    @Autowired
    BilibiliLiveService bilibiliLiveService;

    private String defalutRoomID = "22435648";
    private String xiaoShanRoomID = "23808125";

    @RequestMapping(path = "/danmu", method = RequestMethod.GET)
    public ResponseEntity<String> addDanMu4Live() {
        log.info("bilibili comment create..");
        bilibiliLiveService.addOneDanMuForRoom(defalutRoomID);
        return ResponseEntity.ok("done");
    }


    @RequestMapping(path = "/danmuBatch", method = RequestMethod.GET)
    public ResponseEntity<String> addDanMuBatch4Live() {
        log.info("bilibili comment create..");
        bilibiliLiveService.addBatchDanMuForRoom(defalutRoomID);
        return ResponseEntity.ok("done");
    }


    /**
     * 更改弹幕间隔时间,单位秒
     * @param delay
     * @return
     */
    @RequestMapping(path = "/delayTime", method = RequestMethod.GET)
    public ResponseEntity<String> changeDelayTime4Live(@RequestParam float delay) {
        log.info("bilibili danmu change dalayTIme :{}",delay);
        bilibiliLiveService.setDalayTime(delay);
        return ResponseEntity.ok("done");
    }

    /**
     * 更改直播间ID号
     * @param roomID
     * @return
     */
    @RequestMapping(path = "/changeRoom", method = RequestMethod.GET)
    public ResponseEntity<String> changeDelayTime4Live(@RequestParam String roomID) {
        log.info("bilibili danmu change room :{}",roomID);
        bilibiliLiveService.setRoomID(roomID);
        return ResponseEntity.ok("done");
    }


}
