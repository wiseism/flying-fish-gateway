package com.flying.fish.manage.bean;

import com.flying.fish.formwork.entity.Balanced;
import com.flying.fish.formwork.entity.LoadServer;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Data
public class BalancedReq extends Balanced implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private List<LoadServer> serverList;
}
