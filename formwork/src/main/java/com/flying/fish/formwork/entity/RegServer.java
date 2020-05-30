package com.flying.fish.formwork.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description 客户端注册网关路由服务实体类
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
@Entity
@Table(name="regserver")
@Data
public class RegServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "客户端ID不能为空")
    @Size(min = 2, max = 40, message = "客户端ID长度必需在2到40个字符内")
    @Column(name = "clientId" )
    private String clientId;
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    @Column(name = "routeId")
    private String routeId;
    @Column(name = "status")
    private String status;
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
}
