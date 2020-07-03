package com.flying.fish.formwork.dao;

import com.flying.fish.formwork.entity.LoadServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @Description 负载服务数据层操作接口
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
public interface LoadServerDao extends JpaRepository<LoadServer, Long> {

    /**
     * 删除负载下所有的路由服务
     * @param balancedId
     */
    void deleteAllByBalancedId(Long balancedId);

    /**
     * 查询指定负载下所有路由服务
     * @param balancedId
     * @return
     */
    List<LoadServer> queryByBalancedId(Long balancedId);

    @Query(value = "SELECT r.name,r.routeId,r.groupCode,r.uri,r.path,r.method,l.id,l.weight FROM route r INNER JOIN loadserver l ON r.id=l.routePrimaryId WHERE r.status='0' AND  l.balancedId=?1",
            nativeQuery = true)
    List<Map> queryLoadServerList(Long balancedId);


}
