package com.loki.bi.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.loki.bi.entity.SysCommentEntity;
import com.loki.bi.util.BiliCookieConfig;
import com.loki.bi.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.controller
 * @Description: TODO
 * @date Date : 2023 年 10月 04 日 14:24
 */
@Slf4j
@Service
public class BilibiliCommentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    SysCommentService sysCommentService;


    public void addOneComment(String vid) {
        bilibiliAddCommentForVedio(vid, sysCommentService.findCommentOne());
    }

    public void addBatchComment(String vid) {
        List<SysCommentEntity> sysCommentList = sysCommentService.getCommentSet();
        String msg = "";
        while (sysCommentList.size() > 0) {
            SysCommentEntity com = sysCommentList.get(RandomUtil.randomInt(0, sysCommentList.size()));
            log.info("默认评论: {}", com.getComment());
            msg = com.getComment();
            bilibiliAddCommentForVedio(vid, msg);   //添加评论主题
            sysCommentList.remove(com);
            log.info("set size {}:", sysCommentList.size());
            ThreadUtil.sleep(20000);
        }
    }


    /**
     * 向b站视屏添加评论
     *
     * @param vid 视屏ID
     * @param msg 消息体
     */
    public void bilibiliAddCommentForVedio(String vid, String msg) {
        try {
            String url = "https://api.bilibili.com/x/v2/reply/add";
            String args = "?oid={}&type=1&message={}&plat=1&csrf={}&at_name_to_mid={}"; //大括号这是url自带的传参规则,不是占位符, 规则如下 {@xxx : mid} 对 mid 的用户发起提醒
            args = StrUtil.format(args, vid, msg, BiliCookieConfig.getCSRF());
            url = url.concat(args);
            log.info("bilibili comment reqeust url : {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
            log.info("b 站添加评论返回的消息 :{}", response.getBody());
        } catch (Exception ex) {
            log.info("has an error : {}", ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void getComment4Bvid(String bvid) {
        try {
            String url = "https://api.bilibili.com/x/v2/reply/wbi/main";
            url = url.concat("?").concat(createParam(bvid));
            String body = HttpUtil.get(url);
//            log.info("获取B站视屏评论请求返回体 = {}", body);
            JSONObject fanGson = JSONUtil.parseObj(body);
            JSONObject data = (JSONObject) fanGson.get("data");
            JSONArray replayArray = data.getJSONArray("replies");
            replayArray.forEach((replay) -> {
                JSONObject replayJson = (JSONObject) replay;
                String rpid = replayJson.getStr("rpid");//评论ID
                //https://api.bilibili.com/x/v2/reply/reply?oid=493125311&type=1&root=193695624272&ps=10&pn=1&web_location=333.788
                //通过rpid获取子评论集合
                String oid = replayJson.getStr("oid"); //oid = bvid
                String mid = replayJson.getStr("mid"); //回复人的upid
                JSONObject contentJson = replayJson.getJSONObject("content");//回复内容
                String msg = contentJson.getStr("message"); //回复内容的消息
                JSONArray replay_child = ((JSONObject) replay).getJSONArray("replies");
                log.info("rpid = {}, oid = {}, mid = {},msg ={},子评论数量==={}", rpid, oid, mid, msg, replay_child.size());
            });
            log.info("回复数={}", replayArray.size());
        } catch (Exception ex) {
            log.info("获取视屏下的评论失败, 失败原因:{}", ex.getMessage());
            ex.printStackTrace();
        }
        return;
    }


    /**
     * @param oid 视频ID号
     * @return
     */
    private String createParam(String oid) {
        //pictureHashKey 获取一大堆参数之后生成的一个常数项,不要乱改
        final String pictureHashKey = "ea1db124af3c7062474693fa704f4ff8";
        String wts = String.valueOf(System.currentTimeMillis() / 1000);
        log.info("wts = {}", wts);
//        wts = "1699537879";
//        final String pagination = "{\"offset\":\"{\"type\":1,\"direction\":1,\"session_id\":\"1\",\"data\":{}}\"}";
        final String pagination = "%7B%22offset%22%3A%22%7B%5C%22type%5C%22%3A1%2C%5C%22direction%5C%22%3A1%2C%5C%22session_id%5C%22%3A%5C%221740325652915625%5C%22%2C%5C%22data%5C%22%3A%7B%7D%7D%22%7D";
        TreeMap<String, String> paramMap = new TreeMap<>();
        paramMap.put("mode", "3");
        paramMap.put("oid", oid);
        paramMap.put("pagination_str", pagination);
        paramMap.put("plat", "1");
        paramMap.put("type", "1");
        paramMap.put("web_location", "1315875");
        paramMap.put("wts", wts);
        StringBuilder param = new StringBuilder();
        paramMap.forEach((k, v) -> {
            param.append(k).append("=").append(v).append("&");
        });
        String paramTemp = param.substring(0, param.length() - 1);
//        log.info("param -> {}", paramTemp);
        String sign = MD5.create().digestHex(paramTemp + pictureHashKey);
        log.info("sign = {}", sign);
        param.append("w_rid").append("=").append(sign);
//        log.info("final param : {}",param);
        return param.toString();
    }


    private enum LIKE_COMMENT {
        like, unlike
    }

    /**
     * 对视屏评论区评论点赞或者取消点赞
     *
     * @param rpid   评论ID
     * @param isLike 点赞/取消
     * @param avid   视屏ID
     */
    private void likeComment4Av(String rpid, LIKE_COMMENT isLike, String avid) {
        String base_url = "https://api.bilibili.com/x/v2/reply/action?csrf={}";
        base_url = StrUtil.format(base_url, BiliCookieConfig.getCSRF());
        log.info("点赞视屏评论区的评论请求rul: {}", base_url);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("rpid", rpid);
        paramMap.put("oid", avid);
        paramMap.put("type", "1");
        paramMap.put("ordering", "heat");
        paramMap.put("action", isLike.name().equals(LIKE_COMMENT.like.name()) ? "1" : "0");
        String body = HttpUtil.createPost(base_url).header(HttpHeaders.COOKIE, BiliCookieConfig.cookie).formStr(paramMap).execute().body();
        log.info("body {}", body);
    }

    /**
     * 对视屏评论区添加一条子评论
     * @param rpid  评论id
     * @param msg   消息
     * @param avid  视屏id
     */
    private void addChildComment4Av(String rpid, String msg, String avid) {
        String base_url = "https://api.bilibili.com/x/v2/reply/add?csrf={}";
        base_url = StrUtil.format(base_url, BiliCookieConfig.getCSRF());
        log.info("request url: {}", base_url);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("root", rpid);
        paramMap.put("oid", avid);
        paramMap.put("type", "1");
        paramMap.put("message", msg);
        paramMap.put("plat", "1");
        paramMap.put("parent", rpid);
        paramMap.put("at_name_to_mid", "{}");
        String body = HttpUtil.createPost(base_url).header(HttpHeaders.COOKIE, BiliCookieConfig.cookie).formStr(paramMap).execute().body();
        log.info("body {}", body);
    }

//    public static void main(String[] args) {
//        new BilibiliCommentService().likeComment4Av("193695624272", LIKE_COMMENT.like, "493125311");
//        String msg = "Hello ," + DateUtil.now();
//        new BilibiliCommentService().addChildComment4Av("193695624272", msg, "493125311");
//
//    }

}
