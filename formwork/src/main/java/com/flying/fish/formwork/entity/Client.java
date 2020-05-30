package com.flying.fish.formwork.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description 客户端实体类
 * @Author jianglong
 * @Date 2020/05/15
 * @Version V1.0
 */
@Entity
@Table(name="client")
@Data
public class Client implements java.io.Serializable {
    @Id
    private String id;
    @NotNull(message = "客户端名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name")
    private String name;
    @NotNull(message = "客户端分组不能为空")
    @Column(name = "groupCode")
    private String groupCode;
    @Column(name = "ip")
    private String ip;
    @Column(name = "status")
    private String status;
    @Column(name = "remarks")
    private String remarks;
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
}
