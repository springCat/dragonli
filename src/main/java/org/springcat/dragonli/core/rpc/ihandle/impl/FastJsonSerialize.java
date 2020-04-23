package org.springcat.dragonli.core.rpc.ihandle.impl;

import com.jfinal.json.FastJson;
import org.springcat.dragonli.core.rpc.ihandle.ISerialize;

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
