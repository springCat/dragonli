package org.springcat.dragonli.serialize;

import com.jfinal.json.FastJson;

public class FastJsonSerialize implements ISerialize {
    @Override
    public <T> T decode(String data, Class<T> type) {
        return FastJson.getJson().parse(data,type);
    }

    @Override
    public String encode(Object object) {
        return FastJson.getJson().toJson(object);
    }
}
