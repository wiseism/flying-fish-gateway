package com.flying.fish.manage.bean;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/07/09
 * @Version V1.0
 */
@Data
public class CountReq implements java.io.Serializable {
    private List<String> routeIds;
    private String dateType;
}
