package com.flying.fish.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Description 创建自定义网关路由事件
 * @Author jianglong
 * @Date 2020/05/27
 * @Version V1.0
 */
public class DataRouteApplicationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataRouteApplicationEvent(Object source) {
        super(source);
    }
}
