package org.springcat.dragonli.gateway;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiGateWayFilter extends HttpFilter {

    private final static ApiGateWayConf apiGateWayConf = new ApiGateWayConf().load();

    private final static ApiGatewayInvoke apiGatewayInvoke = ApiGatewayInvokeStarter.initApiGatewayInvoke(apiGateWayConf);

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        apiGatewayInvoke.invokePost(req, res);
    }
}
