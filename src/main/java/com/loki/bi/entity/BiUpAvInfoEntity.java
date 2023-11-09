package com.loki.bi.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi.entity
 * @Description: TODO
 * @date Date : 2023 年 10月 07 日 9:37
 */
@Data
@Entity
@ToString
@Table(name = "bi_up_av_info")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BiUpAvInfoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "upid")
    private String upid;

    @Column(name = "author")
    private String author;

    @Column(name = "avid")
    private String avid;

    @Column(name = "bvid")
    private String bvid;

    @Column(name = "title")
    private String title;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "describle")
    private String describle;

    @Column(name = "comment")
    private Long comment;

    @Column(name = "play")
    private Long play;

    @Column(name = "length")
    private Long length;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "type_id")
    private String typeID;

    @Column(name = "vedio_review")
    private Long vedioReview;

    @Column(name = "coin")
    private Long coin;

    @Column(name = "favorite")
    private Long favorite;

    @Column(name = "av_like")
    private Long like;

    @Column(name = "share")
    private Long share;

    @Column(name = "dan")
    private Long dan;

    @Column(name = "fan")
    private Long fan;

    @Column(name = "elec_month")
    private Long elecMonth;

    @Column(name = "elec_total")
    private Long elecTotal;

    @Column(name = "recommend_index")
    private Float recommendIndex;


}
