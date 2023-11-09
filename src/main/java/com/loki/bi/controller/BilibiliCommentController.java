package com.loki.bi.controller;

import com.loki.bi.service.BilibiliCommentService;
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
 * @Package com.loki.bi
 * @Description: 哔哩哔哩视屏评论操作
 * @date Date : 2023 年 10月 04 日 14:22
 */
@Slf4j
@RestController
@RequestMapping(path = "/comment")
public class BilibiliCommentController {

    @Autowired
    BilibiliCommentService bilibiliCommentService;

    private static final String vid = "489142281";//我自己的


    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public ResponseEntity<String> addComment4Av() {
        log.info("bilibili comment create..");
        bilibiliCommentService.addOneComment(vid);
        return ResponseEntity.ok("done");
    }

    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public ResponseEntity<String> getComment4Av() {
        log.info("bilibili comment get..");
        return ResponseEntity.ok("done");
    }

    @RequestMapping(path = "/batch", method = RequestMethod.GET)
    public ResponseEntity<String> addComment4AvBatch() {
        log.info("bilibili comment create batch..");
        bilibiliCommentService.addBatchComment(vid);
        return ResponseEntity.ok("done");
    }

    @RequestMapping(path = "/getComment4Bvid", method = RequestMethod.GET)
    public ResponseEntity<String> getComment4Bvid(@RequestParam String bvid) {
        log.info("bilibili comment for bvid ..");
        bilibiliCommentService.getComment4Bvid(bvid);
        return ResponseEntity.ok("done");
    }


}
