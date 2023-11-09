package com.loki.bi.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.entity
 * @Description: 系统配置的默认评论
 * @date Date : 2023 年 10月 04 日 18:05
 */
@Data
@Entity
@ToString
@Table(name = "new_rank_hot")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NewRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "mid")
    String mid; //up id

    @Column(name = "face")
    String face; //up 头像图片地址

    @Column(name = "name")
    String name; //up name

    @Column(name = "sex")
    String sex; //性别

    @Column(name = "hot_type")
    String hotType; //热度类型,按周,或者按天

    @Column(name = "coin")
    Long coin;//周期内视屏总的的总投币数

    @Column(name = "fan_grows")
    Long fanGrows; //粉丝增长数

    @Column(name = "level")
    Long level; //up账号等级 1-6

    @Column(name = "like_count")
    Long likeCount; //视屏总点赞数

    @Column(name = "max_sex")
    String maxSex; //性别占比优势一方

    @Column(name = "max_Sex_rate")
    String maxSexRate; //性别优势占比(百分比)

    @Column(name = "new_rank_index")
    String newRankIndex; //新站推荐指数

    @Column(name = "play_count")
    Long playCount; //新站推荐指数


    @Column(name = "type")
    String type; //b站类型区域

    @Column(name = "danmu")
    Long danmu;//视屏弹幕

    //下面的数据均是指周期内最高的视屏的统计数
    @Column(name = "collect_count")
    Long collectCount;//视屏收藏数

    @Column(name = "comments")
    Long comments;//视屏评论数

    @Column(name = "click_count")
    Long clickCount; //播放量

    @Column(name = "cover_url")
    String coverURL;//视屏封面

    @Column(name = "forwar_count")
    Long forwardCount;//转发

    @Column(name = "link_url")
    String linkURL;//视屏地址

    @Column(name = "vedio_like")
    Long like;//视频点赞

    @Column(name = "title")
    String title;//标题

    @Column(name = "bvid")
    String bvid;//视屏短连接,bvid

    @Column(name = "index_date")
    String indexDate;
}
