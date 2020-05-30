package com.flying.fish.manage.bean;

import com.flying.fish.formwork.entity.SecureIp;
import lombok.Data;

/**
 * @Description 对前端请求进行接收与封装bean
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@Data
public class SecureIpReq extends SecureIp implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
