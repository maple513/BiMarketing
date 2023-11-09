package com.loki.bi.controller;

import cn.hutool.core.util.StrUtil;
import com.loki.bi.service.BiUpAvInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.controller
 * @Description: 哔哩哔哩视屏操作
 * @date Date : 2023 年 10月 04 日 14:28
 */

@Slf4j
@RestController
@RequestMapping(path = "/av")
public class BilibiliAvController {

    @Autowired
    BiUpAvInfoService biUpAvInfoService;

    /**
     * 根据用户UPID 查询用户发布的视频信息*
     *
     * @return
     */
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public ResponseEntity<String> saveAvInfoByUp(@RequestParam String upid, @RequestParam String order) {
        if (Objects.isNull(upid)) {
            return ResponseEntity.ok("user's upid is not allow null");
        }
        log.info("user uuid or mid {}", upid);
        upid = StrUtil.isEmpty(upid) ? "1681337756" : upid;
        biUpAvInfoService.upAllAvInfo(upid, CURRENT_PAGE, CUREENT_PAGE_SIZE, BiUpAvInfoService.AV_ORDER_TYPE.valueOf(order));
        return ResponseEntity.ok("done");
    }


    /**
     * 对up主的视屏添加弹幕
     *
     * @param upid
     * @param msg
     * @param avid
     * @return
     */
    @RequestMapping(path = "/danmu4av", method = RequestMethod.GET)
    public ResponseEntity<String> danMu4Av(@RequestParam String upid, String msg, String avid) {
        log.info("视屏所有者,upid : {},视屏 ID avid :{}", upid, avid);
        if (Objects.isNull(upid) || Objects.isNull(avid)) {
            return ResponseEntity.ok("user's name is not allow null");
        }
        biUpAvInfoService.addDanMu4Av(upid, msg, avid, 61); //模拟演示,最终这个参数后台获取,不需要额外指定.
        return ResponseEntity.ok("done");
    }


    /**
     * 对up主的视屏添加批量弹幕
     *
     * @param upid
     * @param avid
     * @return
     */
    @RequestMapping(path = "/danmu4avBatch", method = RequestMethod.GET)
    public ResponseEntity<String> danMu4AvBatch(@RequestParam String upid, String avid) {
        log.info("视屏所有者,upid : {},视屏 ID avid :{}", upid, avid);
        if (Objects.isNull(upid) || Objects.isNull(avid)) {
            return ResponseEntity.ok("user's name is not allow null");
        }
        biUpAvInfoService.addDanMuBatch4Av(upid, avid, 651); //模拟演示,最终不需要视屏时长的参数
        return ResponseEntity.ok("done");
    }

    /**
     * 更改弹幕间隔时间,单位秒
     *
     * @param delay
     * @return
     */
    @RequestMapping(path = "/delayTime", method = RequestMethod.GET)
    public ResponseEntity<String> changeDelayTime4Live(@RequestParam float delay) {
        log.info("bilibili danmu change dalayTIme :{}", delay);
        biUpAvInfoService.setDelayTime(delay);
        return ResponseEntity.ok("done");
    }

    private static final int CURRENT_PAGE = 1;
    private static final int CUREENT_PAGE_SIZE = 50;


    /**
     * @param avid 视屏id
     * @param like 1 : like 0 : unlike
     * @return
     */
    @RequestMapping(path = "/likeAv", method = RequestMethod.GET)
    public ResponseEntity<String> addLike4av(@RequestParam String avid) {
        log.info("like someone up's av ,of course unlike");
        log.info("avid={}", avid);
        biUpAvInfoService.likeAndUnlikeAv(avid, Boolean.TRUE);
        return ResponseEntity.ok("done");
    }

    /**
     * 给up主所有视屏点赞
     *
     * @param upid
     * @return
     */
    @RequestMapping(path = "/likeAllUpAv", method = RequestMethod.GET)
    public ResponseEntity<String> Like4UpAllAv(@RequestParam String upid) {
        log.info("给B站UP主: {} 所有视屏都点赞", upid);
        biUpAvInfoService.doLike4UpAllAv(upid, Boolean.TRUE);
        return ResponseEntity.ok("done");
    }


    /**
     * 给up主所有视屏取消点赞
     *
     * @param upid
     * @return
     */
    @RequestMapping(path = "/unLikeAllUpAv", method = RequestMethod.GET)
    public ResponseEntity<String> unLike4UpAllAv(@RequestParam String upid) {
        log.info("给B站UP主: {} 所有视屏都取消点赞", upid);
        biUpAvInfoService.doLike4UpAllAv(upid, Boolean.FALSE); //
        return ResponseEntity.ok("done");
    }



    /**
     * 给指定的avid 的视屏下添加一条评论
     *
     * @param avid
     * @return
     */
    @RequestMapping(path = "/addCom2Av", method = RequestMethod.GET)
    public ResponseEntity<String> addCom2Av(@RequestParam String avid) {
        log.info("给指定的avid :{} 的视屏下添加一条评论", avid);
        biUpAvInfoService.addComment2Av(avid); //
        return ResponseEntity.ok("done");
    }




    /**
     * 给up主所有视屏添加随机评论
     *
     * @param upid
     * @return
     */
    @RequestMapping(path = "/addCom2UpAllAv", method = RequestMethod.GET)
    public ResponseEntity<String> addCom2UpAllAv(@RequestParam String upid) {
        log.info("给B站UP主: {} 所有视屏添加随机评论", upid);
        biUpAvInfoService.addComment2UpAllAv(upid);
        return ResponseEntity.ok("done");
    }






}
