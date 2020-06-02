package com.flying.fish.gateway.event;

import com.flying.fish.formwork.dao.RouteDao;
import com.flying.fish.formwork.entity.Route;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.gateway.cache.RouteCache;
import com.flying.fish.gateway.service.LoadRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 注册网关路由事件，动态加载数据库网关路由信息，监听路由配置事件，重新动态加载网关路由数据
 * @Author jianglong
 * @Date 2020/05/27
 * @Version V1.0
 */
@Slf4j
@Component
public class DataRouteApplicationEventListen implements RouteDefinitionRepository,ApplicationEventPublisherAware {
    @Resource
    private RouteDao routeDao;
    @Resource
    private LoadRouteService loadRouteService;

    private ApplicationEventPublisher publisher;
    private List<RouteDefinition> routeDefinitions = new ArrayList<>();

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 监听事件刷新配置；
     * 1.CustomApplicationEvent发布后，即触发listenEvent事件方法；
     * 2.publisher.publishEvent方法执行RefreshRoutesEvent事件后；
     * 3.gateway加调用getRouteDefinitions方法重新获取网关路由集合配置并刷新内存
     */
    @EventListener(classes = DataRouteApplicationEvent.class)
    public void listenEvent() {
        initLoadRoute();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 获取所有网关路由信息
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitions);
    }

    @Deprecated
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return Mono.defer(() -> Mono.error(new NotFoundException("此save方法不提供功能实现，请勿调用")));
    }

    @Deprecated
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.defer(() -> Mono.error(new NotFoundException("此delete方法不提供功能实现，请勿调用")));
    }

    @PostConstruct
    public void initLoadRoute(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        //一定要清空routeDefinitions否则每次刷新会往集合中添加重复数据
        routeDefinitions.clear();
        try {
            List<Route> list = routeDao.findAll(Example.of(query));
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(r -> {
                    RouteCache.put(r.getRouteId(), r);
                    routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
                });
            }
            log.info("监听到网关路由配置发生变更，重新加载网关路由配置共{}条", routeDefinitions.size());
        }catch(Exception e){
            log.error("加载数据库中网关路由配置异常：",e);
        }
    }

}
