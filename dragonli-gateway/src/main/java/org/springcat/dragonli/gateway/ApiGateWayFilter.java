package org.springcat.dragonli.gateway;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiGateWayFilter extends HttpFilter {


    private  static ApiGatewayInvoke apiGatewayInvoke;

    public void init() throws ServletException {
        apiGatewayInvoke = ApiGatewayInvokeStarter.init();
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        apiGatewayInvoke.invoke(req, res);
    }
}
