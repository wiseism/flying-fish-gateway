package com.flying.fish.gateway.service;

import com.flying.fish.gateway.event.ApplicationEventPublisherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Description 动态添加路由
 * @Author jianglong
 * @Date 2020/05/11
 * @Version V1.0
 */
@Service
public class DynamicRouteService {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private ApplicationEventPublisherFactory publisherApplicationEventFactory;

    /**
     * 增加路由
     * @param definition
     * @return
     */
    public void add(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            publisherApplicationEventFactory.refreshRoutesEvent();
        } catch (Exception e) {
            throw new IllegalArgumentException("添加网关路由失败", e);
        }
    }

    /**
     * 更新路由,删除》添加
     * @param definition
     * @return
     */
    public void update(RouteDefinition definition) {
        try {
            delete(definition.getId());
        } catch (Exception e) {
            throw new IllegalArgumentException("更新网关路由失败", e);
        }
        try {
            add(definition);
        } catch (Exception e) {
            throw new IllegalArgumentException("更新网关路由失败", e);
        }
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    public void delete(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id)).
                    then(Mono.defer(() -> Mono.just(ResponseEntity.ok().build()))).
                    onErrorResume((t) -> t instanceof NotFoundException, (t) -> Mono.just(ResponseEntity.notFound().build())).
                    subscribe();
        }catch(Exception e){
            throw new IllegalArgumentException("删除网关路由"+ id +"失败", e);
        }
    }

    /**
     * 刷新路由，通过spring的事件监听机制，发布事件，触发监听方法的执行
     * @return
     */
    public void fresh(String type){
        publisherApplicationEventFactory.publisherEvent(type);
    }
}
