package com.flying.fish.formwork.service;

import com.flying.fish.formwork.base.BaseService;
import com.flying.fish.formwork.dao.RegServerDao;
import com.flying.fish.formwork.dao.RouteDao;
import com.flying.fish.formwork.entity.RegServer;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.PageResult;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description 注册服务业务管理类
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
@Service
public class RegServerService extends BaseService<RegServer,Long,RegServerDao> {

    @Autowired
    private RegServerDao regServerDao;

    /**
     * 停止客户端下所有路由服务的访问（状态置为1，禁止通行）
     * @param clientId
     */
    public void stopClientAllRoute(String clientId){
        regServerDao.setClientAllRouteStatus(clientId,Constants.YES,Constants.NO);
    }

    /**
     * 启动客户端下所有路由服务的访问（状态置为0，允许通行）
     * @param clientId
     */
    public void startClientAllRoute(String clientId){
        regServerDao.setClientAllRouteStatus(clientId,Constants.NO,Constants.YES);
    }

    /**
     * 停止路由服务下所有客户端的访问（状态置为1，禁止通行）
     * @param routeId
     */
    public void stopRouteAllClient(String routeId){
        regServerDao.setRouteAllClientStatus(routeId,Constants.YES,Constants.NO);
    }

    /**
     * 启动路由服务下所有客户端的访问（状态置为0，允许通行）
     * @param routeId
     */
    public void startRouteAllClient(String routeId){
        regServerDao.setRouteAllClientStatus(routeId,Constants.NO,Constants.YES);
    }

    /**
     * 查询当所有已注册的客户端
     * @return
     */
    public List allRegClientList(){
        return regServerDao.allRegClientList();
    }

    /**
     * 查询当前网关路由服务下已注册的客户端
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult clientPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT s.id AS regServerId,s.status AS regServerStatus,DATE_FORMAT(s.createTime,'%Y-%m-%d %H:%i:%s') as regServerTime,c.* FROM Client c, RegServer s WHERE c.id = s.clientId AND s.routeId=?";
        return pageNativeQuery(sql, Arrays.asList(regServer.getRouteId()), currentPage, pageSize);
    }

    /**
     * 查询当前客户端已注册的网关路由服务
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult serverPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT s.id AS regServerId,s.status as regServerStatus,DATE_FORMAT(s.createTime,'%Y-%m-%d %H:%i:%s') as regServerTime,r.* FROM Route r, RegServer s WHERE r.routeId = s.routeId and s.clientId=?";
        return pageNativeQuery(sql, Arrays.asList(regServer.getClientId()), currentPage, pageSize);
    }

    /**
     * 查询当前网关路由服务下没有注册的客户端
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notRegClientPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT c.id,c.name,c.groupCode,c.ip FROM Client c WHERE c.status='0' AND id NOT IN (SELECT s.clientId FROM Regserver s WHERE s.routeId=?)";
        return pageNativeQuery(sql, Arrays.asList(regServer.getRouteId()), currentPage, pageSize);
    }

    /**
     * 查询当前客户端没有注册的网关路由服务
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notRegServerPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT r.id,r.routeId,r.name,r.uri,r.path,r.status FROM Route r WHERE r.status='0' AND r.routeId NOT IN (SELECT s.routeId FROM Regserver s WHERE s.clientId=?)";
        return pageNativeQuery(sql, Arrays.asList(regServer.getClientId()), currentPage, pageSize);
    }


}
