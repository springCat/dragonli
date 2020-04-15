package org.springcat.dragonli.jfinal;

import com.jfinal.kit.JsonKit;
import org.springcat.dragonli.serialize.ISerialize;

public class JFinalJsonSerialize implements ISerialize {

    @Override
    public <T> T decode(String data, Class<T> type) {
        return JsonKit.parse(data,type);
    }

    @Override
    public String encode(Object object) {
        return  JsonKit.toJson(object);
    }
}
