package com.loki.bi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.config
 * @Description: TODO
 * @date Date : 2023 年 06月 30 日 12:21
 */

@Service
@Slf4j
public class LoadDataAtStartUpConfig {

    static List<String> sensitive = new ArrayList<String>();

    static {
        sensitive.add("毒品");
        sensitive.add("人口贩卖");
        sensitive.add("赌博");
    }

    //bilibili板块
    public static Map<String, String> biliBlockType = new HashMap<String, String>();

    static {
        biliBlockType.put("1", "生活");
        biliBlockType.put("2", "游戏");
        biliBlockType.put("3", "知识");
        biliBlockType.put("4", "动画");
        biliBlockType.put("5", "娱乐");
        biliBlockType.put("6", "音乐");
        biliBlockType.put("7", "影视");
        biliBlockType.put("8", "时尚");
        biliBlockType.put("9", "汽车");
        biliBlockType.put("10", "舞蹈");
        biliBlockType.put("11", "美食");
        biliBlockType.put("12", "动物圈");
        biliBlockType.put("13", "科技");
        biliBlockType.put("14", "运动");
        biliBlockType.put("15", "鬼畜");
        biliBlockType.put("16", "国创");
        biliBlockType.put("17", "数码");
        biliBlockType.put("18", "番剧");
        biliBlockType.put("19", "纪录片");
        biliBlockType.put("20", "电影");
        biliBlockType.put("21", "电视剧");
    }

    @PostConstruct
    void loadData() {
        sensitive.add("初始化敏感词库");
        log.info("load data success : [{}]", sensitive.toString());
    }

}
