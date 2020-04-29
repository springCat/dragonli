package org.springcat.dragonli.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

public abstract class JsonController extends Controller {

    /**
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getJsonBean(Class<T> cls) {
        T jsonBean = getAttr("$jsonbean$");

        if(jsonBean != null){
            return jsonBean;
        }

        String rawData = getRawData();
        if(StrKit.isBlank(rawData)){
            return jsonBean;
        }

        return JsonKit.parse(rawData, cls);
    }
}
