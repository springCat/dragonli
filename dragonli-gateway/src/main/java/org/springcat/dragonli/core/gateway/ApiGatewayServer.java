package org.springcat.dragonli.core.gateway;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import lombok.SneakyThrows;

import javax.servlet.DispatcherType;


public class ApiGatewayServer {

    @SneakyThrows
    public static void main(final String[] args) {


        DeploymentInfo deploymentInfo = Servlets.deployment();
        deploymentInfo.setDeploymentName("ApiGateway");
        deploymentInfo.setContextPath("/");
        deploymentInfo.setClassLoader(Undertow.class.getClassLoader());

        deploymentInfo.addFilter(
                Servlets.filter("ApiGateway", ApiGateWayFilter.class)
        ).addFilterUrlMapping("ApiGateway", "/*", DispatcherType.REQUEST);


        DeploymentManager deploymentManager =  Servlets.defaultContainer().addDeployment(deploymentInfo);
        deploymentManager.deploy();
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(deploymentManager.start()).build();
        server.start();
    }
}