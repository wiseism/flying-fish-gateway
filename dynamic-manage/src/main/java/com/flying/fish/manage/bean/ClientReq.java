package com.flying.fish.manage.bean;

import com.flying.fish.formwork.entity.Client;
import lombok.Data;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
@Data
public class ClientReq extends Client implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
