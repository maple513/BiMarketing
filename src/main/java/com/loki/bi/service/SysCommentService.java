package com.loki.bi.service;

import cn.hutool.core.util.RandomUtil;
import com.loki.bi.entity.SysCommentEntity;
import com.loki.bi.respostiory.SysCommentRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class SysCommentService {

    @Autowired
    SysCommentRespository sysCommentRespository;

    public String findCommentOne() {
        List<SysCommentEntity> comms = getCommentSet();
        return comms.get(RandomUtil.randomInt(0, comms.size())).getComment();
    }

    public List<SysCommentEntity> getCommentSet() {
        return sysCommentRespository.findAll();
    }
}
