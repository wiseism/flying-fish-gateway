package com.flying.fish.gateway.event;

import com.flying.fish.formwork.util.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @Description 发布事件，触发监听事件的方法
 * @Author jianglong
 * @Date 2020/06/02
 * @Version V1.0
 */
@Lazy
@Slf4j
@Component
public class ApplicationEventPublisherFactory implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 刷新路由，通过spring的事件监听机制，发布事件，触发监听方法的执行
     * @return
     */
    public void publisherEvent(String type){
        if (StringUtils.equals(type, RouteConstants.IP)) {
            this.publisher.publishEvent(new DataIpApplicationEvent(this));
        }else if (StringUtils.equals(type, RouteConstants.CLIENT_ID)){
            this.publisher.publishEvent(new DataClientApplicationEvent(this));
        } else if (StringUtils.equals(type, RouteConstants.ROUTE)) {
            this.publisher.publishEvent(new DataRouteApplicationEvent(this));
        } else {
            throw new IllegalArgumentException("发布事件错误，参数格式不正确");
        }
    }

    /**
     * 刷新网关路由
     */
    public void refreshRoutesEvent(){
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

}
