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
@Table(name = "new_rank_hot_task")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NewRankTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "hot_type")
    String hotType; //up id

    @Column(name = "task_value")
    String taskValue; //统计的时间,按起始时间计算

    @Column(name = "block_name")
    String blockName;//板块名称

    @Column(name = "md5")
    String md5;

}
