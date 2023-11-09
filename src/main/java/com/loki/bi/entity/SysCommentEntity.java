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
@Table(name = "sys_comment")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SysCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;


    @Column(name = "comment")
    String comment;

}
