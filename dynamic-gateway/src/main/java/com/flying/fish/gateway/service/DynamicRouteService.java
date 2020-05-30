package com.flying.fish.gateway.service;

import com.flying.fish.formwork.util.RouteConstants;
import com.flying.fish.gateway.event.DataClientApplicationEvent;
import com.flying.fish.gateway.event.DataIpApplicationEvent;
import com.flying.fish.gateway.event.DataRouteApplicationEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
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
public class DynamicRouteService implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    public void add(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
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
        if (StringUtils.equals(type, RouteConstants.IP)) {
            this.publisher.publishEvent(new DataIpApplicationEvent(this));
        }else if (StringUtils.equals(type, RouteConstants.CLIENT_ID)){
            this.publisher.publishEvent(new DataClientApplicationEvent(this));
        } else if (StringUtils.equals(type, RouteConstants.ROUTE)) {
            this.publisher.publishEvent(new DataRouteApplicationEvent(this));
        } else {
            throw new IllegalArgumentException("刷新网关路由，参数格式不正确");
        }
    }
}
