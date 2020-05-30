package com.flying.fish.formwork.dao;

import com.flying.fish.formwork.entity.RegServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description 注册网关服务Dao数据层操作接口
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
public interface RegServerDao extends JpaRepository<RegServer, Long> {

    /**
     * 修改客户端下所有已注册网关服务的状态
     * @param clientId
     * @param status
     * @param newStatus
     */
    @Query(value = "update RegServer set status=?3 where clientId=?1 and status=?2")
    @Modifying
    @Transactional(rollbackFor = {Throwable.class})
    void setClientAllRouteStatus(String clientId, String status, String newStatus);

    /**
     * 修改网关服务下所有已注册客户端的状态
     * @param routeId
     * @param status
     * @param newStatus
     */
    @Query(value = "update RegServer set status=?3 where routeId=?1 and status=?2")
    @Modifying
    @Transactional(rollbackFor = {Throwable.class})
    void setRouteAllClientStatus(String routeId, String status, String newStatus);

    /**
     * 查询指定网关服务下的注册的
     * @return
     */
    @Query(value ="SELECT s.routeId,c.id,c.ip FROM Client c, RegServer s WHERE c.id = s.clientId AND c.status='0' AND s.status='0'")
    List allRegClientList();

}
