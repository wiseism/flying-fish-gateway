package com.flying.fish.formwork.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description 网关路由实体类
 * @Author jianglong
 * @Date 2020/05/11
 * @Version V1.0
 */
@Entity
@Table(name="route")
@Data
public class Route implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "网关路由名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name" )
    private String name;
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    @Column(name = "routeId")
    private String routeId;
    @NotNull(message = "客户端分组不能为空")
    @Column(name = "groupCode")
    private String groupCode;
    @NotNull(message = "网关路由服务uri不能为空")
    @Size(min = 2, max = 40, message = "网关路由服务uri长度必需在2到200个字符内")
    @Column(name = "uri")
    private String uri;
    @NotNull(message = "网关路由断言Path不能为空")
    @Size(min = 2, max = 40, message = "网关路由断言Path长度必需在2到100个字符内")
    @Column(name = "path")
    private String path;
    @Column(name = "method")
    private String method;
    //ip,token,id
    @Column(name = "filterGatewaName")
    private String filterGatewaName;
    //hystrix,custom
    @Column(name = "filterHystrixName")
    private String filterHystrixName;
    //ip,uri,requestId
    @Column(name = "filterRateLimiterName")
    private String filterRateLimiterName;
    //header,ip,param,time,cookie
    @Column(name = "filterAuthorizeName")
    private String filterAuthorizeName;
    @Column(name = "fallbackMsg")
    private String fallbackMsg;
    @Column(name = "fallbackTimeout")
    private Long fallbackTimeout;
    @Column(name = "replenishRate")
    private Integer replenishRate;
    @Column(name = "burstCapacity")
    private Integer burstCapacity;
    @Column(name = "weight")
    private Integer weight;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    @Column(name = "stripPrefix")
    private Integer stripPrefix;
    @Column(name = "requestParameter")
    private String requestParameter;
    @Column(name = "accessHeader")
    private String accessHeader;
    @Column(name = "accessIp")
    private String accessIp;
    @Column(name = "accessParameter")
    private String accessParameter;
    @Column(name = "accessTime")
    private String accessTime;
    @Column(name = "accessCookie")
    private String accessCookie;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updateTime")
    private Date updateTime;
}
