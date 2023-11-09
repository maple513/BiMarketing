package com.loki.bi.service;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.loki.bi.util.BiliCookieConfig;
import com.loki.bi.entity.SysCommentEntity;
import com.loki.bi.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.controller
 * @Description: up主直播间弹幕
 * @date Date : 2023 年 10月 04 日 14:24
 */
@Slf4j
@Service
public class BilibiliLiveService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    SysCommentService sysCommentService;

    public void addOneDanMuForRoom(String roomID) {
        String msg = sysCommentService.findCommentOne();
        bilibiliAddDanMuForVedio(roomID, msg.length() > 20 ? msg.substring(0, 19) : msg);
    }

    private float delayTime = 3f;

    private String roomID = "";

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setDalayTime(float dt) {
        this.delayTime = dt;
    }

    public void addBatchDanMuForRoom(String roomID) {
        this.roomID = roomID;
        log.info("弹幕直播间ID : {}", roomID);
        List<SysCommentEntity> sysCommentList = sysCommentService.getCommentSet();
        String msg = "";
        while (sysCommentList.size() > 0) {
            SysCommentEntity com = sysCommentList.get(RandomUtil.randomInt(0, sysCommentList.size()));
            log.info("默认评论: {}", com.getComment());
            msg = com.getComment().length() > 20 ? com.getComment().substring(0, 19) : com.getComment();
            bilibiliAddDanMuForVedio(this.roomID, msg);   //添加评论主题
            sysCommentList.remove(com);
            log.info("set size {}:", sysCommentList.size());
            ThreadUtil.sleep((int) delayTime * 1000);
        }
    }


    /**
     * 向b站直播发送弹幕
     *
     * @param roomID 直播间ID
     * @param msg    消息体
     */
    public void bilibiliAddDanMuForVedio(String roomID, String msg) {
        log.info("弹幕直播间 room ID :{}, 消息 msg : {}", roomID, msg);
        try {
            String url = "https://api.live.bilibili.com/msg/send";
            String args = "?bubble=0&msg={}&color=16777215&mode=1&room_type=0&jumpfrom=86001&reply_mid=0&fontsize=25&rnd=1696260358&roomid={}&csrf={}&csrf_token={}";
            args = StrUtil.format(args, msg, roomID, BiliCookieConfig.getCSRF(),BiliCookieConfig.getCSRF());
            url = url.concat(args);
            log.info("bilibili comment reqeust url : {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
            log.info("b 站添加评论返回的消息 :{}", response.getBody());
        } catch (Exception ex) {
            log.info("has an error : {}", ex.getMessage());
            ex.printStackTrace();
        }
    }
}
