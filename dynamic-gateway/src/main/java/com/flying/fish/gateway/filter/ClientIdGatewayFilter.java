package com.flying.fish.gateway.filter;

import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.HttpResponseUtils;
import com.flying.fish.formwork.util.NetworkIpUtils;
import com.flying.fish.formwork.util.RouteConstants;
import com.flying.fish.gateway.cache.ClientIdCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Description 客户端ID过滤
 * @Author jianglong
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class ClientIdGatewayFilter implements GatewayFilter, Ordered {

    private String routeId;

    public ClientIdGatewayFilter(String routeId){
        this.routeId = routeId;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientId = this.getClientId(request);
        if (this.isPassClientId(routeId, clientId)){
            String ip = NetworkIpUtils.getIpAddress(request);
            String msg = "客户端ID无权限访问网关路由："+ routeId +"! Id: " + clientId + ",Ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 获取请求头部的clientId值
     * @param request
     * @return
     */
    public String getClientId(ServerHttpRequest request){
        HttpHeaders headers = request.getHeaders();
        String clientId = request.getQueryParams().getFirst(RouteConstants.CLIENT_ID);
        if (StringUtils.isBlank(clientId)){
            clientId = headers.getFirst(RouteConstants.CLIENT_ID);
        }
        return clientId;
    }

    /**
     * 是否允许通行客户端ID
     * @return
     */
    public boolean isPassClientId(String routeId, String clientId){
        List<String> ids = (List<String>)ClientIdCache.get(routeId);
        return ids != null && ids.contains(clientId);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
