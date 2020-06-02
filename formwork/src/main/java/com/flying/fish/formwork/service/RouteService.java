package com.flying.fish.formwork.service;

import com.flying.fish.formwork.base.BaseService;
import com.flying.fish.formwork.dao.RegServerDao;
import com.flying.fish.formwork.dao.RouteDao;
import com.flying.fish.formwork.entity.RegServer;
import com.flying.fish.formwork.entity.Route;
import com.flying.fish.formwork.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Description 路由管理业务类
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Service
public class RouteService extends BaseService<Route,Long,RouteDao> {

    @Resource
    private RegServerService regServerService;

    /**
     * 删除网关路由以及已注册的客户端（关联表）
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(Long id){
        Route route = this.findById(id);
        if (route != null) {
            RegServer regServer = new RegServer();
            regServer.setRouteId(route.getRouteId());
            List<RegServer> regServerList = regServerService.findAll(regServer);
            this.delete(route);
            if (regServerList != null && regServerList.size()>0) {
                regServerService.deleteInBatch(regServerList);
            }
        }
    }

}
