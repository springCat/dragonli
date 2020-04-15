package org.springcat.dragonli.jfinal;

import com.jfinal.kit.HttpKit;
import org.springcat.dragonli.client.IHttpTransform;

import java.util.Map;

public class JFinalHttpTransform implements IHttpTransform {
    @Override
    public String post(String url, String data, Map<String, String> headers) {
        return HttpKit.post(url, data, headers);
    }
}
