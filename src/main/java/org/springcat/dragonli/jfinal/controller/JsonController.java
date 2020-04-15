package org.springcat.dragonli.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

public abstract class JsonController extends Controller {

    public <T> T getJsonBean() {
        T jsonBean = getAttr("$jsonbean");
        if(jsonBean != null){
            return jsonBean;
        }
        return (T) JsonKit.parse(getRawData(), jsonBean.getClass());
    }
}
