package com.loki.bi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.loki.bi.util.BiliCookieConfig;
import com.loki.bi.entity.BiUpAvInfoEntity;
import com.loki.bi.entity.SysCommentEntity;
import com.loki.bi.respostiory.BiUpAvInfoRespository;
import com.loki.bi.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.controller
 * @Description: b站 av 视屏操作 ,关于视屏内弹幕批量发送时间间隔不能低于 1秒
 * @date Date : 2023 年 10月 04 日 14:24
 */
@Slf4j
@Service
public class BiUpAvInfoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SysCommentService sysCommentService;

    @Autowired
    private BiUpAvInfoRespository biUpAvInfoRespository;

    /**
     * 获取Up主所有视屏信息
     *
     * @param upid       up's uuid
     * @param pn         当前页
     * @param ps         每页大小
     * @param order_type
     */
    public void upAllAvInfo(String upid, int pn, int ps, AV_ORDER_TYPE order_type) {
        List<BiUpAvInfoEntity> upAvList = new ArrayList<BiUpAvInfoEntity>();
        getUpNextPageAvInfo(upAvList, upid, pn, ps, order_type);
        biUpAvInfoRespository.saveAll(upAvList);
    }

    /**
     * 递归获取up所有视屏信息
     *
     * @param upAvList
     * @param upid
     * @param pn
     * @param ps
     * @param order_type
     */
    private void getUpNextPageAvInfo(List<BiUpAvInfoEntity> upAvList, String upid, int pn, int ps, AV_ORDER_TYPE order_type) {
        String url = "https://api.bilibili.com/x/space/wbi/arc/search?mid={}&order={}&pn={}&ps={}&index=1&platform=web&web_location=1550101&w_rid=09e54595ce6cc69abcf2425992dd3&wts=1696547353";
        if (StrUtil.isEmpty(upid)) return;
        url = StrUtil.format(url, upid, order_type.name(), pn, ps);
        log.info("b站 up 视屏信息 请求连接: {}", url);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
        if (StrUtil.isEmpty(response.getBody())) return;
        JSONObject upJSON = JSONUtil.parseObj(response.getBody());
        //返回数据结构处理流
        String code = upJSON.get("code").toString();
        if (!StrUtil.equals(code, "0")) {
            log.info("获取Up主视屏信息异常,异常提醒: {}", upJSON.get("message").toString());
        }
        JSONObject vedio = (JSONObject) upJSON.get("data");
        JSONObject vedioList = (JSONObject) vedio.get("list");

        //vedioList.get("tlist");
        //vedioList.get("slist"); 这两个节点内容,暂不清楚,挖坑,后续再填
        JSONArray vlist = (JSONArray) vedioList.get("vlist");
        if (CollectionUtil.isNotEmpty(vlist)) {
            parse(upAvList, vlist);
            log.info("size: {}", upAvList.size());
            //分页信息
            JSONObject page = (JSONObject) vedio.get("page");
            int curentPage = NumberUtil.parseInt(page.get("pn").toString());//当前页
            int size = NumberUtil.parseInt(page.get("ps").toString());//每页大小
            int count = NumberUtil.parseInt(page.get("count").toString());//总数
            // 首次请求未请求完全部分,全部抓取
            if (curentPage * size < count) {
                curentPage++;
                getUpNextPageAvInfo(upAvList, upid, curentPage, size, order_type);  //递归调用
            }
        }
    }


    /**
     * 数据结构私有解析体
     *
     * @param upAvList
     * @param vlist
     */
    private void parse(List<BiUpAvInfoEntity> upAvList, JSONArray vlist) {
        for (int i = 0; i < vlist.size(); i++) {
            BiUpAvInfoEntity avEntity = new BiUpAvInfoEntity();
            JSONObject vinfo = (JSONObject) vlist.get(i);
            avEntity.setTypeID(vinfo.get("typeid").toString());
            avEntity.setComment(NumberUtil.parseLong(vinfo.get("comment").toString()));
            avEntity.setPlay(!NumberUtil.isNumber(vinfo.get("play").toString()) ? 0 : NumberUtil.parseLong(vinfo.get("play").toString()));
            avEntity.setAuthor(vinfo.get("author").toString());
            avEntity.setSubTitle(vinfo.get("subtitle").toString());
            avEntity.setDescrible(vinfo.get("description").toString());
            avEntity.setTitle(vinfo.get("title").toString());
            String upid = vinfo.get("mid").toString();
            avEntity.setUpid(upid);
            if (!StrUtil.isEmpty(upid)) {
                upElec(upid, avEntity);
                upFan(upid, avEntity);
            }
            avEntity.setCreateDate(vinfo.get("created").toString());
            avEntity.setLength(timeTransSecond(vinfo.get("length").toString()));
            avEntity.setVedioReview(NumberUtil.parseLong(vinfo.get("video_review").toString()));
            avEntity.setAvid(vinfo.get("aid").toString());
            String bvid = vinfo.get("bvid").toString();
            if (!StrUtil.isEmpty(bvid)) {
                hotArgs(avEntity.getAvid(), bvid, avEntity);
            }
            avEntity.setBvid(bvid);
            upAvList.add(avEntity);
            log.info(avEntity.toString());
        }
    }


    /**
     * 通过bvid 获取 视屏点赞,分享,投币,弹幕,收藏等数据
     *
     * @param bvid
     * @param entity
     * @avid 截取字符串的锚点定位
     */
    private void hotArgs(String avid, String bvid, BiUpAvInfoEntity entity) {
        String url = "https://www.bilibili.com/video/{}/?spm_id_from=333.999.0.0";
        if (StrUtil.isEmpty(bvid) || null == entity) return;
        url = StrUtil.format(url, bvid);
        log.info("up 点赞,投币,分享,收藏,转发 请求连接: {}", url);
        String body = HttpRequest.get(url).execute().body();
//        log.info("request  body {}", body);  //响应报文太多,影响爬取熟读,注释掉
        try {
            StringReader sr = StrUtil.getReader(body);
            BufferedReader br = new BufferedReader(sr);
            String bufferLine = null;
            //"stat":{"aid:xxxx" 这个作为锚点,具备唯一性,无论是单个数据包,或者批量数据包.
            String anchor = StrUtil.concat(true, "\"stat\":{\"aid\":", avid);
            //截取起始
            String anchor_begin = StrUtil.concat(true, "\"stat\":");
            //截取终止范围
            String anchor_end = StrUtil.concat(true, ",\"dynamic\":");
            while ((bufferLine = br.readLine()) != null) {
//              log.info("行读取内容: {}", bufferLine);  //行数据太大,注释掉
                if (StrUtil.contains(bufferLine, anchor)) {
                    String content = StrUtil.subBetween(bufferLine, anchor_begin, anchor_end);
                    log.info("锚点数据 : {}", content);
                    JSONObject argsObje = JSONUtil.parseObj(content);
                    if (JSONUtil.isNull(argsObje)) break;
                    //投币
                    entity.setCoin(NumberUtil.parseLong(argsObje.get("coin").toString()));
                    //收藏
                    entity.setFavorite(NumberUtil.parseLong(argsObje.get("favorite").toString()));
                    //弹幕
                    entity.setDan(NumberUtil.parseLong(argsObje.get("danmaku").toString()));
                    //分享
                    entity.setShare(NumberUtil.parseLong(argsObje.get("share").toString()));
                    //点赞
                    entity.setLike(NumberUtil.parseLong(argsObje.get("like").toString()));
                    //计算视屏热点推荐指数
                    recommendHot(entity);

                    ThreadUtil.sleep(300);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("解析网页出现异常:{}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 通过upid 获取 up 当月充电人次 以及 累计充电人次
     *
     * @param upid
     * @param entity
     */
    private void upElec(String upid, BiUpAvInfoEntity entity) {
        String url = "https://api.bilibili.com/x/ugcpay-rank/elec/month/up?up_mid={}&gaia_source=main_web&web_location=333.999&w_rid=134bb7f782b6606bcce47a45999fc281&wts=1696926093";
        if (StrUtil.isEmpty(upid) || null == entity) return;
        url = StrUtil.format(url, upid);
        log.info("up 充电人数 请求连接: {}", url);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
//        log.info("fans response : {}", response.getBody().toString());
        JSONObject elecGson = JSONUtil.parseObj(response.getBody());
        JSONObject elecObject = (JSONObject) elecGson.get("data");
        if (ObjectUtil.isNotEmpty(elecObject)){
            if (ObjectUtil.isNotEmpty(elecObject.get("count"))) {
                entity.setElecMonth(NumberUtil.parseLong(elecObject.get("count").toString()));
            }
            if (ObjectUtil.isNotEmpty(elecObject.get("total_count"))) {
                entity.setElecTotal(NumberUtil.parseLong(elecObject.get("total_count").toString()));
                log.info("up elec month = {},and elec total {}", elecObject.get("count").toString(), elecObject.get("total_count").toString());
            }
        }
    }

    /**
     * 通过upid 获取 up 粉丝数
     *
     * @param upid
     * @param entity
     */
    private void upFan(String upid, BiUpAvInfoEntity entity) {
        if (StrUtil.isEmpty(upid) || null == entity) return;
        String url = "https://api.bilibili.com/x/relation/stat?vmid={}&web_location=333.999&w_rid=7fd534b19dfc2f6ac67f814842f9f0ea&wts=1696925418";
        url = StrUtil.format(url, upid);
        log.info("up fans 请求连接: {}", url);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
//        log.info("fans response : {}", response.getBody().toString());
        JSONObject fanGson = JSONUtil.parseObj(response.getBody());
        JSONObject fanObject = (JSONObject) fanGson.get("data");
        log.info("up fans = {}", fanObject.get("follower").toString());
        entity.setFan(NumberUtil.parseLong(fanObject.get("follower").toString()));
    }


    /**
     * 将时长转换成秒 , 例如: xxx:yy:zz = 3600xxx + 60yy + zz
     *
     * @param time
     * @return
     */
    private static long timeTransSecond(String time) {
        if (StrUtil.isEmpty(time)) return 0L;
        long[] hms = StrUtil.splitToLong(time, ":");
        if (hms.length > 2) {  //数组大于2 ,存在小时数,数组大于1,存在分钟数
            return 3600 * hms[0] + 60 * hms[1] + hms[2];
        }
        if (hms.length > 1) {
            return 60 * hms[0] + hms[1];
        }
        return hms[0];
    }


    public enum AV_ORDER_TYPE {
        click,
        bubdate,
        stow
    }

    /**
     * 对up主视频添加一条弹幕
     *
     * @param upid
     * @param msg
     * @param avid
     * @param timeLength 视屏时长,随机生成弹幕的时间戳,对应的字段为 progress ,生成规则 xx秒xxx毫秒
     */
    public void addDanMu4Av(String upid, String msg, String avid, int timeLength) {
        if (StrUtil.isEmpty(upid) || StrUtil.isEmpty(avid)) return;
        msg = StrUtil.isEmpty(msg) ? sysCommentService.findCommentOne() : msg;
        //视屏内弹幕字数不能超过100个中文字符
        msg = msg.length() > 100 ? msg.substring(0, 99) : msg;
        String url = "https://api.bilibili.com/x/v2/dm/post";
        //up,msg,avid
        String args = "?color={}&fontsize=25&pool=0&mode=1&type=1&oid={}&msg={}&aid={}&progress={}&rnd=2&plat=1&checkbox_type=0&colorful=&polaris_appid=100&polaris_platfrom=5&spmid=333.788.0.0&from_spmid=333.999.0.0&csrf={}";
        int progress = createProgress(timeLength - 2);   //默认最后不追加弹幕
        args = StrUtil.format(args, createColor(), upid, msg, avid, progress, BiliCookieConfig.getCSRF());
        url = url.concat(args);
        log.info("add bilibli danmu request url :{}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
        log.info("b 站添加视屏弹幕返回的消息 :{}", response.getBody());
    }

    /**
     * 根据传入视屏的时长,生成随机数
     *
     * @param timeLength
     * @return
     */
    public int createProgress(int timeLength) {
        //b站视屏描述时间是 xx:xx:xx 在爬取转存的时候,最好能转换成单位为秒的int时长
        int second = RandomUtil.randomInt(0, timeLength);
        int mills = RandomUtil.randomInt(0, 999);
        return 1000 * second + mills;
    }

    /**
     * RGB 颜色介于 #111111 - #FFFFFF = 1118481 - 16777215
     *
     * @return
     */
    public int createColor() {
        return RandomUtil.randomInt(1118481, 16777215);
    }

    //定义弹幕时间间隔
    private float delayTime = 15f;

    public void setDelayTime(float dt) {
        this.delayTime = dt;
    }

    /**
     * 对指定用户UPID,AVID批量弹幕
     *
     * @param upid
     * @param avid
     */
    public void addDanMuBatch4Av(String upid, String avid, int avTime) {
        List<SysCommentEntity> sysCommentList = sysCommentService.getCommentSet();
        String msg = "";
        while (sysCommentList.size() > 0) {
            SysCommentEntity com = sysCommentList.get(RandomUtil.randomInt(0, sysCommentList.size()));
            log.info("默认评论: {}", com.getComment());
            msg = com.getComment();
            addDanMu4Av(upid, msg, avid, avTime);   //添加单条弹幕
            sysCommentList.remove(com);
            log.info("set size {}:", sysCommentList.size());
            ThreadUtil.sleep((int) delayTime * 1000);
        }
    }

    static final String likeRamval = "9";
    static final String likeVale = "1";

    static final String unlikeRamval = "11";
    static final String unlikeValue = "2";

    /**
     * 对视屏点赞或者取消点赞
     *
     * @param avid
     * @param like true: like ,false : unlike
     */
    public void likeAndUnlikeAv(String avid, boolean like) {
        int like_value = 2;
        String ramval = "9";
        if (like) {
            like_value = 1;
            ramval = "11";
        }
        String url = "https://api.bilibili.com/x/web-interface/archive/like?aid={}&like={}&eab_x=1&ramval={}&source=web_normal&ga=1&csrf={}";
        url = StrUtil.format(url, avid, like_value, ramval, BiliCookieConfig.getCSRF());
        log.info("like avid request url : {}", url);
        ResponseEntity<String> body = restTemplate.postForEntity(url, RestTemplateUtil.createBiliHttpEntityWithCookie(), String.class);
        log.info("response body : {}", body.getBody().toString());
    }


    /**
     * 对up主所有已发布视屏点赞或者取消点赞
     *
     * @param upid
     * @param like
     */
    public void doLike4UpAllAv(String upid, boolean like) {
        Set<String> avidSet = biUpAvInfoRespository.findAllByUpid(upid).stream().map(BiUpAvInfoEntity::getAvid).collect(Collectors.toSet());
        for (String avid : avidSet) {
            likeAndUnlikeAv(avid, like);
            ThreadUtil.sleep(8 * 1000);
        }
    }


    @Autowired
    BilibiliCommentService bilibiliCommentService;

    /**
     * 对某个视屏他添加一条随机评论
     *
     * @param avid
     */
    public void addComment2Av(String avid) {
        bilibiliCommentService.addOneComment(avid);
    }

    /**
     * 对up主的所有视屏添加一条评论
     *
     * @param upid
     */
    public void addComment2UpAllAv(String upid) {
        Set<String> avidSet = biUpAvInfoRespository.findAllByUpid(upid).stream().map(BiUpAvInfoEntity::getAvid).collect(Collectors.toSet());
        for (String avid : avidSet) {
            addComment2Av(avid);
            ThreadUtil.sleep(8 * 1000);
        }
    }

    /**
     * 热点视屏推荐权重,算法来源于B站开源,数据来源于2019 这里只做参考
     * 现存数据中已经引入了复播的概念,这个的权重理应更高.但是此算法中并没有复播的概念
     * 投币权重 x 0.4f
     * 收藏权重 x 0.3f
     * 弹幕权重 x 0.4f
     * 评论权重 x 0.4f
     * 播放权重 x 0.25f
     * 点赞权重 x 0.4f
     * 分享权重 x0.4f
     *
     * @param avs
     */
    private static void recommendHot(BiUpAvInfoEntity avs) {
        if (null == avs) return;
        float recommend = 0f;
        recommend = avs.getCoin() * 0.4f + avs.getFavorite() * 0.3f + avs.getDan() * 0.4f + avs.getComment() * 0.4f + avs.getPlay() * 0.25f + avs.getLike() * 0.4f + avs.getShare() * 0.6f;
        //考虑到数据来源均是已收录很久的过往视屏.公平起见统一按照一天内的视屏发布权重.
        avs.setRecommendIndex(recommend * 1.5f);
    }

}
