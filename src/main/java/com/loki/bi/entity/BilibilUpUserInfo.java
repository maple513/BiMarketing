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
 * @date Date : 2023 年 10月 04 日 14:33
 */


@Data
@Entity
@ToString
@Table(name = "bi_user_info")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class BilibilUpUserInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    String upName;

}
