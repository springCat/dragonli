package org.springcat.dragonli.core.rpc.ihandle.impl;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.IHttpTransform;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * 默认使用httpclient 单例连接池方式
 */
public class HttpclientTransform implements IHttpTransform {

    private final static Log log = LogFactory.get();

    static final int DEFAULT_MAX_CONNECTIONS = 1000;
    static final int DEFAULT_MAX_PER_ROUTE_CONNECTIONS = 500;
    static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 5; // 3 sec
    static final int DEFAULT_READ_TIMEOUT = 1000  * 10; // 3 sec
    static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 1000 * 5; //5 sec

    private final HttpClient httpClient;

    protected HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpclientTransform(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpclientTransform() {
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

    @Override
    public String post(String url, Map<String, String> headers, String request) throws RpcException {
        TimeInterval timeInterval = new TimeInterval();
        String response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            addHeadersToRequest(httpPost, headers);
            HttpResponse httpResponse = null;
            httpPost.setEntity(new StringEntity(request));
            httpResponse = httpClient.execute(httpPost);
            if(isSuccess(httpResponse.getStatusLine().getStatusCode())) {
                response = IoUtil.read(httpResponse.getEntity().getContent(), Charset.defaultCharset());
            }
        } catch (Exception e) {
            log.error("HttpclientTransform invoke error url:{},headers:{},request:{},error:{}" ,url,headers,request,e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_TRANSFORM_INVOKE.getCode());
        }finally {
            log.info("rpc invoke url:{},header:{},req:{},resp{},cost:{}",url,headers,request,response,timeInterval.interval());
        }
        return response;
    }

    private void addHeadersToRequest(HttpRequestBase request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> headerValue : headers.entrySet()) {
            request.addHeader(headerValue.getKey(), headerValue.getValue());
        }
    }

}
