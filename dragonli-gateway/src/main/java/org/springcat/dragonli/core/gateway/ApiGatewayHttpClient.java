package org.springcat.dragonli.core.gateway;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springcat.dragonli.core.exception.RpcException;
import org.springcat.dragonli.core.exception.RpcExceptionCodes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description HttpClient
 * @Author springCat
 * @Date 2020/5/15 17:29
 */
public class ApiGatewayHttpClient {

    private final static Log log = LogFactory.get();

    static final int DEFAULT_MAX_CONNECTIONS = 1000;
    static final int DEFAULT_MAX_PER_ROUTE_CONNECTIONS = 500;
    static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 5; // 3 sec
    static final int DEFAULT_READ_TIMEOUT = 1000 * 10; // 3 sec
    static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 1000 * 5; //5 sec

    private final org.apache.http.client.HttpClient httpClient;


    public ApiGatewayHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE_CONNECTIONS);

        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT).
                setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT).
                setSocketTimeout(DEFAULT_READ_TIMEOUT).
                build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().
                setConnectionManager(connectionManager).
                setDefaultRequestConfig(requestConfig).
                useSystemProperties();

        this.httpClient = httpClientBuilder.build();
    }

    /**
     *
     * 直接copy body stream,减少内存消耗和解析的性能开销
     * @param url
     * @param request
     * @param response
     * @return
     * @throws RpcException
     */
    public String proxyPost(String url, HttpServletRequest request, HttpServletResponse response) throws RpcException {
        TimeInterval timeInterval = new TimeInterval();
        try {
            HttpPost httpPost = new HttpPost(url);
            pushHeadersToRequest(httpPost, request);
            httpPost.setEntity(new InputStreamEntity(request.getInputStream()));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (isSuccess(httpResponse.getStatusLine().getStatusCode())) {
                pullHeadersFromResponse(httpResponse, response);
                IoUtil.copy(httpResponse.getEntity().getContent(), response.getOutputStream());
            }
        } catch (Exception e) {
            log.error("HttpclientTransform invoke error url:{},error:{}", url, e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_TRANSFORM_INVOKE.getCode());
        } finally {
            log.info("rpc invoke url:{},response{},cost:{}", url, response, timeInterval.interval());
        }
        return null;
    }

    public void pushHeadersToRequest(HttpRequest httpRequest, HttpServletRequest httpServletRequest) {
        //只透传固定的header todo 后续这边可以做个配置参数
        httpRequest.addHeader("x-uid",httpServletRequest.getHeader("x-uid"));
    }

    public void pullHeadersFromResponse(HttpResponse httpResponse, HttpServletResponse httpServletResponse) {
        //只透传固定的header todo 后续这边可以做个配置参数
    }

    boolean isSuccess(int httpCode) {
        return httpCode >= 200 && httpCode < 300;
    }

}
