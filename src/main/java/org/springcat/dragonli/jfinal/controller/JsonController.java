package org.springcat.dragonli.jfinal.controller;

import cn.hutool.core.io.IoUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpServletRequest;

public abstract class JsonController extends Controller {

    /**
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getJsonBean(Class<T> cls) {
        T jsonBean = getAttr("$jsonbean");

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
