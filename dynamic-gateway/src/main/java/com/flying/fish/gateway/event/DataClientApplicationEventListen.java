package com.flying.fish.gateway.event;

import com.flying.fish.formwork.service.ClientService;
import com.flying.fish.formwork.service.RegServerService;
import com.flying.fish.gateway.cache.ClientIdCache;
import com.flying.fish.gateway.cache.RegIpListCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@Slf4j
@Component
public class DataClientApplicationEventListen {

    @Resource
    private RegServerService regServerService;

    /**
     * 监听事件刷新配置；
     * DataIpApplicationEvent发布后，即触发listenEvent事件方法；
     */
    @EventListener(classes = DataClientApplicationEvent.class)
    public void listenEvent() {
        List list = regServerService.allRegClientList();
        ClientIdCache.clear();
        RegIpListCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            Iterator iterator = list.iterator();
            String routeId ;
            String ip;
            String id;
            Object [] object;
            List<String> ips;
            List<String> ids;
            while (iterator.hasNext()){
                object = (Object[]) iterator.next();
                routeId = String.valueOf(object[0]);
                //添加网关路由注册的客户端ID
                id = String.valueOf(object[1]);
                ids = (List<String>) ClientIdCache.get(routeId);
                if (CollectionUtils.isEmpty(ids)) {
                    ids = new ArrayList<>();
                }
                ids.add(id);
                ClientIdCache.put(routeId, ids);

                //添加网关路由注册的客户端IP
                ip = String.valueOf(object[2]);
                ips = (List<String>) RegIpListCache.get(routeId);
                if (CollectionUtils.isEmpty(ips)) {
                    ips = new ArrayList<>();
                }
                ips.add(ip);
                RegIpListCache.put(routeId, ips);
            }
        }
        log.info("监听到客户端配置发生变更，重新加载客户端配置共{}条", size);
    }

}
