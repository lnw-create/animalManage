package com.hutb.gateway.filter;

import com.hutb.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();

        //2.判断请求类型，登录和注册不进行拦截
        if (request.getPath().toString().contains("login") || request.getPath().toString().contains("register")){
            //放行
            return chain.filter(exchange);
        }

        //3.获取token
        String token = "";
        List<String> headers = request.getHeaders().get("token");
        if (headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }else {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        //4.判断token是否存在
        if (token == null || token.isEmpty()){
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        //5.解析token并判断token是否合法
        Map<String, Object> claims = null;
        try {
            claims = JwtUtil.parseJwt(token, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8FgCz6/n59Z6VX5xtzvQ4aCU2oIqxERUd/Qk5uVQ2WMZS6OfmvmP3ZQ+Oo+2y1E+W8yaZTSVXVI2ztNxJJNkMSQX+uCv3+6FbX6W//R/1DhXD7XkXiPx2+6NgljEiKCw+7g1y4UlywX1m0JDlPSqphGyWTybD4m37Xy/cJwIDAQAB");
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        String username1 = claims.get("username").toString();

        //6. 修改发送给微服务的请求头，加入用户信息
        ServerWebExchange username = exchange.mutate()
                .request(builder -> builder.header("username", username1))
                .build();

        //7. 放行，调用下一个过滤器
        return chain.filter(username);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
