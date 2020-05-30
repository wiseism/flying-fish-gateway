package com.flying.fish.gateway.event;

import com.flying.fish.formwork.entity.SecureIp;
import com.flying.fish.formwork.service.SecureIpService;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.gateway.cache.IpListCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@Slf4j
@Component
public class DataIpApplicationEventListen{
    @Resource
    private SecureIpService secureIpService;

    /**
     * 监听事件刷新配置；
     * DataIpApplicationEvent发布后，即触发listenEvent事件方法；
     */
    @EventListener(classes = DataIpApplicationEvent.class)
    public void listenEvent() {
        SecureIp secureIp = new SecureIp();
        secureIp.setStatus(Constants.YES);
        List<SecureIp> list = secureIpService.findAll(secureIp);
        IpListCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            list.forEach(s -> IpListCache.put(s.getIp(), s.getStatus().equals(Constants.YES)));
        }
        log.info("监听到IP配置发生变更，重新加载IP配置共{}条", size);
    }

}
