package com.flying.fish.gateway.filter;

import com.flying.fish.formwork.util.HttpResponseUtils;
import com.flying.fish.formwork.util.NetworkIpUtils;
import com.flying.fish.formwork.util.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description 验证用户是否携带token(token的有效性与正确性，由服务端验证，网关只验证是否存在)
 * @Author jianglong
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class TokenGatewayFilter implements GatewayFilter, Ordered {

    private String routeId;

    public TokenGatewayFilter(String routeId){
        this.routeId = routeId ;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = getToken(request);
        if (StringUtils.isBlank(token)){
            String ip = NetworkIpUtils.getIpAddress(request);
            String msg = "客户端Token值为空，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 获取请求header中带的token
     * @param request
     * @return
     */
    public String getToken(ServerHttpRequest request){
        HttpHeaders headers = request.getHeaders();
        //验证是否带token
        String token = request.getQueryParams().getFirst(RouteConstants.TOKEN);
        if (StringUtils.isBlank(token)){
            token = headers.getFirst(RouteConstants.TOKEN);
        }
        return token;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
