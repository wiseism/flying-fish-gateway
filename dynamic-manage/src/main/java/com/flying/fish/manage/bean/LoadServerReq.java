package com.flying.fish.manage.bean;

import com.flying.fish.formwork.entity.LoadServer;
import lombok.Data;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Data
public class LoadServerReq extends LoadServer implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
