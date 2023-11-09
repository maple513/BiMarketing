package com.loki.bi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.loki.bi.config.LoadDataAtStartUpConfig;
import com.loki.bi.entity.NewRankEntity;
import com.loki.bi.entity.NewRankTaskEntity;
import com.loki.bi.respostiory.NewRankHotRespository;
import com.loki.bi.respostiory.NewRankHotTaskRespository;
import com.loki.bi.util.NumberFormat;
import com.loki.bi.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.service
 * @Description: TODO
 * @date Date : 2023 年 10月 06 日 3:22
 */
@Slf4j
@Service
public class NewRankService {

    public static enum CYCLE_TYPE {
        week, day
    }

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    NewRankHotRespository newRankHotRespository;
    @Autowired
    NewRankHotTaskRespository newRankHotTaskRespository;


    /**
     * 获取b站所有板块的热点视屏
     */
    public void biliHotAll(CYCLE_TYPE cycleType) {
        List<NewRankTaskEntity> rankTask = newRankHotTaskRespository.findAll();
        initConfigData();
        LoadDataAtStartUpConfig.biliBlockType.forEach((k, v) -> {
                    log.info("[{},{}]", k, v);
                    if (cycleType.name().equals(CYCLE_TYPE.week.name())) {
                        biliHotByWeek(rankTask, cycleType, k);
                    } else {
                        biliHotByDay(rankTask, cycleType, k);
                    }
                }
        );
    }

    //
    private String buildID;//初始值为空,第一次请求后填充,后续每次请求必须要带上此参数
    private String weekTask;//每周
    private String startTime;//起始时间
    private String endTime; //终止时间
    private String dayTask;//每日

    //获取热点视屏需要初始化的数据
    private void initConfigData() {
        String base = "https://newrank.cn/ranklist/bilibili";
        ResponseEntity<String> response = restTemplate.getForEntity(base, String.class);
        String body = response.getBody();
        String anchor_begin = "<script id=\"__NEXT_DATA__\" type=\"application/json\">";
        String anchor_end = "</script>";
        String content = StrUtil.subBetween(body, anchor_begin, anchor_end);
        JSONObject contentJson = JSONUtil.parseObj(content);
        buildID = contentJson.get("buildId").toString();
        log.info("buildid ={}", buildID);
        JSONArray weekList = contentJson.getJSONObject("props").getJSONObject("pageProps").getJSONObject("value").getJSONObject("timeData").getJSONArray("weekList");
        log.info(weekList.toString());
        weekTask = weekList.getJSONObject(0).get("value").toString();
        dayTask = contentJson.getJSONObject("props").getJSONObject("pageProps").getJSONObject("value").getJSONObject("timeData").getJSONArray("dayList").getJSONObject(0).get("value").toString();
        startTime = weekList.getJSONObject(0).get("start").toString();
        endTime = weekList.getJSONObject(0).get("end").toString();
    }


    public void biliHotByWeek(List<NewRankTaskEntity> rankTask, CYCLE_TYPE cycleType, String biliBlock) {
        if (StrUtil.isEmpty(weekTask)) {
            log.info("周热度初始资源未正常加载,终止流程");
            return;
        }
        if (CollectionUtil.isNotEmpty(rankTask)) {
            Set md5Set = rankTask.stream().map(NewRankTaskEntity::getMd5).collect(Collectors.toSet());
            String weekDd5 = MD5.create().digestHex(cycleType.name().concat(weekTask).concat(LoadDataAtStartUpConfig.biliBlockType.get(biliBlock)).getBytes());
            if (md5Set.contains(weekDd5)) {
                log.info("{}-{}-{},已存在,无需重复请求", cycleType.name(), weekTask, LoadDataAtStartUpConfig.biliBlockType.get(biliBlock));
                return;
            }
        }
        requestRankList(cycleType, biliBlock);
    }

    /**
     * 每日榜单
     *
     * @param rankTask
     * @param cycleType
     * @param biliBlock
     */
    public void biliHotByDay(List<NewRankTaskEntity> rankTask, CYCLE_TYPE cycleType, String biliBlock) {
        if (StrUtil.isEmpty(dayTask)) {
            log.info("天初始资源为正常加载,终止流程");
            return;
        }
        if (CollectionUtil.isNotEmpty(rankTask)) {
            Set md5Set = rankTask.stream().map(NewRankTaskEntity::getMd5).collect(Collectors.toSet());
            String dayMd5 = MD5.create().digestHex(cycleType.name().concat(dayTask).concat(LoadDataAtStartUpConfig.biliBlockType.get(biliBlock)).getBytes());
            if (md5Set.contains(dayMd5)) {
                log.info("{}-{}-{},已存在,无需重复请求", cycleType.name(), dayTask, LoadDataAtStartUpConfig.biliBlockType.get(biliBlock));
                return;
            }
        }
        requestRankList(cycleType, biliBlock);
    }


    //获取表单数据
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void requestRankList(CYCLE_TYPE cycleType, String biliBlock) {
        final String weekDayCount = "7";
        final String DayTCount = "1";
        String url = "https://newrank.cn/_next/data/{}/ranklist/bilibili/{}/{}/{}.json?slug={}&slug={}&slug={}";
        String cycle = StrUtil.equals(cycleType.name(), CYCLE_TYPE.week.name()) ? weekDayCount : DayTCount;
        if (CYCLE_TYPE.week.equals(cycleType)) {
            url = StrUtil.format(url, buildID, biliBlock, cycle, weekTask, biliBlock, cycle, weekTask);
        } else {
            url = StrUtil.format(url, buildID, biliBlock, cycle, dayTask, biliBlock, cycle, dayTask);
        }
        ResponseEntity<String> hotListResponse = restTemplate.getForEntity(url, String.class);
        JSONObject hotObj = JSONUtil.parseObj(hotListResponse.getBody());
        JSONArray hotArray = hotObj.getJSONObject("pageProps").getJSONObject("value").getJSONArray("rankList");
        log.info("=========================week list info :=========================");
        log.info("weekArray Length = {}", hotArray.size());
        String vedioURL = "https://gw.newrank.cn/api/mainRank/nr/mainRank/rank/work/searchWork";
        boolean taskExcute = Boolean.TRUE;
        List<NewRankEntity> newRankList = new ArrayList<NewRankEntity>();
        for (Object o : hotArray) {
            JSONObject obj = (JSONObject) o;
            NewRankEntity newRank = new NewRankEntity();
            newRank.setIndexDate(cycleType.name().equals(CYCLE_TYPE.week.name()) ? weekTask : dayTask);
            newRank.setHotType(cycleType.name());
            assembleNewRank(obj, newRank);
            String mid = obj.get("mid").toString();
            String type = obj.get("type").toString();
            Map<String, String> payLoad = createPayLoad(mid, cycleType, type);
            ResponseEntity<String> vedioResponse = restTemplate.postForEntity(vedioURL, RestTemplateUtil.createNewRankHttpEntity(payLoad), String.class);
            JSONObject weekJsonObj = JSONUtil.parseObj(vedioResponse.getBody());
            if (StrUtil.equals(weekJsonObj.getStr("code"), "999")) {
                log.info("目标主机消息返回:[{}]", weekJsonObj.getStr("msg"));
                taskExcute = Boolean.FALSE;
                break;
            }
            JSONObject vedioJson = (JSONObject) weekJsonObj.getJSONObject("data").getJSONArray("workDetailVos").get(0);
            assembleNewRankByVedio(vedioJson, newRank);
            log.info("obj = {}", newRank.toString());
            ThreadUtil.sleep(1000);
            newRankList.add(newRank);
            log.info("成功获取记录数={}", newRankList.size());
        }
        newRankHotRespository.saveAllAndFlush(newRankList);
        if (taskExcute) {
            NewRankTaskEntity newRankTask = new NewRankTaskEntity();
            newRankTask.setHotType(cycleType.name());
            newRankTask.setTaskValue(cycleType.name().equals(CYCLE_TYPE.week.name()) ? weekTask : dayTask);
            newRankTask.setBlockName(LoadDataAtStartUpConfig.biliBlockType.get(biliBlock));
            String md5 = MD5.create().digestHex(newRankTask.getHotType().concat(newRankTask.getTaskValue()).concat(newRankTask.getBlockName()).getBytes());
            newRankTask.setMd5(md5);
            newRankHotTaskRespository.save(newRankTask);
        }
    }

    /**
     * 装配up主周期内最高热度视屏的概要信息
     *
     * @param vedioJson
     * @param rank
     */
    private void assembleNewRankByVedio(JSONObject vedioJson, NewRankEntity rank) {
        rank.setCollectCount(NumberUtil.parseLong(vedioJson.getStr("collectCount")));
        rank.setComments(NumberUtil.parseLong(vedioJson.getStr("comments")));
        rank.setClickCount(NumberUtil.parseLong(vedioJson.getStr("clickCount")));
        rank.setCoverURL(vedioJson.getStr("coverUrl"));
        rank.setForwardCount(NumberUtil.parseLong(vedioJson.getStr("forwardCount")));
        rank.setLinkURL(vedioJson.getStr("linkUrl"));
        rank.setLike(NumberUtil.parseLong(vedioJson.getStr("likeCount")));
        rank.setTitle(vedioJson.getStr("title"));//标题
        rank.setBvid(vedioJson.getStr("workId"));//bvid
    }

    /**
     * 装配up主周期内的数据概要
     *
     * @param json
     * @param rank
     */
    private void assembleNewRank(JSONObject json, NewRankEntity rank) {
        rank.setCoin(NumberFormat.parseNumber(json.getStr("coinCount")));
        rank.setFace(json.get("face").toString());
        rank.setFanGrows(NumberFormat.parseNumber(json.getStr("followerDiff")));
        rank.setLevel(NumberUtil.parseLong(json.get("level").toString()));
        rank.setLikeCount(NumberFormat.parseNumber(json.get("likeCount").toString()));
        JSONObject maxSex = json.getJSONObject("maxSex");
        if (!JSONUtil.isNull(maxSex)) {
            rank.setMaxSex(maxSex.getStr("key"));
            rank.setMaxSexRate(maxSex.getStr("rate"));
        }
        rank.setMid(json.getStr("mid"));
        rank.setName(json.getStr("name"));
        rank.setNewRankIndex(json.getStr("newrankIndex"));
        rank.setPlayCount(NumberFormat.parseNumber(json.getStr("playCount")));
        rank.setSex(json.getStr("sex"));
        rank.setType(json.getStr("type"));
        rank.setDanmu(NumberFormat.parseNumber(json.getStr("videoReviewCount"))); //这明明就是复播,但页面上显示弹幕,看数据确实是弹幕
    }

    //payload 数据包,根据cycleType类型构建不同的week,day的请求数据包
    private Map<String, String> createPayLoad(String mid, CYCLE_TYPE cycleType, String type) {
        Map<String, String> reqBody = new HashMap<String, String>();
        reqBody.put("accountId", mid);
        reqBody.put("account", StrUtil.EMPTY);
        reqBody.put("timeDimension", cycleType.name());
        reqBody.put("platformType", "6");
        //构建Week时,起始时间和终止时间不一致是一个时间区间
        if (cycleType.equals(CYCLE_TYPE.week)) {
            reqBody.put("startTime", startTime);
            reqBody.put("endTime", endTime);
        } else {
            //构建day时,起始和终止时间是同一个
            reqBody.put("startTime", dayTask);
            reqBody.put("endTime", dayTask);
        }
        reqBody.put("rankName", type);
        reqBody.put("type", cycleType.name().equals(CYCLE_TYPE.week.name()) ? "1" : "0");
        reqBody.put("rankType", "0");
        reqBody.put("currentRank", "2");
        reqBody.put("sort", "newrankIndex");
        log.info("request body {}", reqBody.toString());
        return reqBody;
    }


    /**
     * 新站视屏榜单操作需要用到oid即视屏id,需要将bvid资源id转换成oid
     *
     * @param bvURL 视屏资源路径
     * @return
     */
    String bvidTransOID(String bvURL) {
        if (StrUtil.isEmpty(bvURL)) return null;
        if (!StrUtil.endWith(bvURL, "/")) {
            bvURL = bvURL + "/";
        }
        String body = HttpUtil.get(bvURL);
//        log.info("response body ={}", body);
        String anchor_begin = "\"aid\":";
        String anchor_end = "\"bvid\":";
        String content = StrUtil.subBetween(body, anchor_begin, anchor_end);
        log.info("content = [{}]", content);
        return content.substring(0, content.length() - 1);
    }

    public static void main(String[] args) {
        String url = "https://www.bilibili.com/video/BV1ea4y1D74k";
        String oid = new NewRankService().bvidTransOID(url);
        log.info("oid = [{}]", oid);
    }
}
