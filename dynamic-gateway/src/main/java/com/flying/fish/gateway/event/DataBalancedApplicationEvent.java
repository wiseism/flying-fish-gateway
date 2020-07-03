package com.flying.fish.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/07/03
 * @Version V1.0
 */
public class DataBalancedApplicationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataBalancedApplicationEvent(Object source) {
        super(source);
    }
}
