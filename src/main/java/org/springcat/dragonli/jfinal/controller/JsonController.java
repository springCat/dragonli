package org.springcat.dragonli.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

public abstract class JsonController extends Controller {

    public <T> T getJsonBean(Class<T> beanClass) {
        T jsonBean = getAttr("$jsonBean");
        if(jsonBean != null){
            return  jsonBean;
        }
        return JsonKit.parse(getRawData(), beanClass);
    }
}
