package com.hutb.gateway.filter;

import com.hutb.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    
    // 角色常量定义
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String ROLE_NORMAL_ADMIN = "normal_admin";
    private static final String ROLE_NORMAL_USER = "normal_user";
    private static final String ROLE_VOLUNTEER = "volunteer";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        //2.判断请求类型，登录和注册不进行拦截
        if (path.contains("login") || path.contains("register")){
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
        
        String username = claims.get("username").toString();
        String role = claims.get("role").toString();
        Long userId = ((Number) claims.get("userId")).longValue();
        
        //6. 权限校验
        if (!hasPermission(path, role)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }

        //7. 修改发送给微服务的请求头，加入用户信息
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder.header("username", username).header("userId", String.valueOf(userId)).header("role", role))
                .build();

        //8. 放行，调用下一个过滤器
        return chain.filter(modifiedExchange);
    }
    
    /**
     * 权限校验方法
     * @param path 请求路径
     * @param role 用户角色
     * @return 是否有权限
     */
    private boolean hasPermission(String path, String role) {
        // 超管拥有所有权限
        if (ROLE_SUPER_ADMIN.equals(role)) {
            return true;
        }

        // 普通管理员权限：可管理自己、普通用户及志愿者
        if (ROLE_NORMAL_ADMIN.equals(role)) {
            if (path.contains("/admin/employee/editEmployee")) { // 管理自己的员工信息
                return true;
            }
            if (path.contains("/admin/employee/queryEmployeeList")) { // 可以查看员工列表
                return true;
            }
            if (path.contains("/admin/user/")) { // 管理普通用户
                return true;
            }
            if (path.contains("/admin/volunteer/")) { // 管理志愿者
                return true;
            }
            if (path.contains("/volunteerActivity")) { // 管理志愿活动
                return true;
            }
            if (path.contains("/shopping")) { // 管理志愿活动
                return true;
            }
            if (path.contains("/pet")) { // 管理宠物
                return true;
            }

        }

        // 普通用户权限：仅可管理自己的信息，可查看志愿者活动（只读）
        if (ROLE_NORMAL_USER.equals(role)) {
            if (path.contains("/admin/user/editUser")) { // 编辑自己的用户信息
                return true;
            }
            if (path.contains("/volunteerActivity/queryActivityList")) { // 查看志愿活动列表（只读）
                return true;
            }
        }

        // 志愿者权限：可参与志愿活动服务以及商品服务
        if (ROLE_VOLUNTEER.equals(role)) {
            if (path.contains("/admin/user/editUser")) { // 编辑自己的用户信息
                return true;
            }
            if (path.contains("/volunteerActivity/queryActivityList")) { // 查看志愿活动列表（只读）
                return true;
            }
        }

        // 默认情况下，只有匹配以上规则的请求才能通过
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
