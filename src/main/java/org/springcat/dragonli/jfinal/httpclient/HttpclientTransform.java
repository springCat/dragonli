package org.springcat.dragonli.jfinal.httpclient;
import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ecwid.consul.v1.health.model.HealthService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springcat.dragonli.core.rpc.IHttpTransform;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpclientTransform implements IHttpTransform {

    private static final Log log = LogFactory.get(HttpclientTransform.class);

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
    public Object post(RpcRequest rpcRequest, HealthService healthService){
        HttpPost httpPost = new HttpPost(genUrl(rpcRequest,healthService));
        addHeadersToRequest(httpPost, rpcRequest.getRpcHeader());
        HttpResponse httpResponse = null;
        try {
            String req = rpcRequest.getSerialize().encode(rpcRequest.getRequestObj());
            httpPost.setEntity(new StringEntity(req));
            httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                String resp = IoUtil.read(httpResponse.getEntity().getContent(), Charset.defaultCharset());
                return rpcRequest.getSerialize().decode(resp, rpcRequest.getReturnType());
            }
        } catch (IOException e) {
            log.error("RpcRequest:{},error:{}",rpcRequest,e.getMessage());
        }
        return null;
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
