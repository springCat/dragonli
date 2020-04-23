package org.springcat.dragonli.core.rpc.ihandle.impl;
import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springcat.dragonli.core.rpc.exception.TransformException;
import org.springcat.dragonli.core.rpc.ihandle.IHttpTransform;
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
    public String post(String url, Map<String, String> headers, String request) throws TransformException {
        try {
            HttpPost httpPost = new HttpPost(url);
            addHeadersToRequest(httpPost, headers);
            HttpResponse httpResponse = null;
            httpPost.setEntity(new StringEntity(request));
            httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                String resp = IoUtil.read(httpResponse.getEntity().getContent(), Charset.defaultCharset());
                return resp;
            }
            return null;
        } catch (Exception e) {
            throw new TransformException(e.getMessage());
        }
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
