package com.flying.fish.formwork.dao;

import com.flying.fish.formwork.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description 网关服务Dao数据层操作接口
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
public interface RouteDao extends JpaRepository<Route, String> {

}
