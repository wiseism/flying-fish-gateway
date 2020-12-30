package com.flying.fish.gateway.filter;

import com.flying.fish.formwork.util.HttpResponseUtils;
import com.flying.fish.formwork.util.NetworkIpUtils;
import com.flying.fish.gateway.cache.ClientIdCache;
import com.flying.fish.gateway.cache.IpListCache;
import com.flying.fish.gateway.cache.RegIpListCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @Description IP过滤
 * @Author jianglong
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class IpGatewayFilter implements GatewayFilter, Ordered {

    private String routeId;
    public IpGatewayFilter(String routeId){
        this.routeId = routeId;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = NetworkIpUtils.getIpAddress(request);
        if (!this.isPassIp(ip)){
            String msg = "此IP已被限制，无权限访问网关!" +" ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        if (!this.regPassIp(routeId, ip)){
            String msg = "客户端IP无权限访问网关路由："+ routeId +"! ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 是否允许通行IP
     * @return
     */
    private boolean isPassIp(String ip){
        Object object = IpListCache.get(ip);
        //如果没有设置IP白名单，则不做限制
        if (object == null){
            return true;
        }
        return (boolean) object;
    }

    /**
     * 是否注册通行IP
     * @return
     */
    private boolean regPassIp(String routeId,String ip){
        List<String> ips = (List<String>)RegIpListCache.get(routeId);
        return ips != null && ips.contains(ip);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
