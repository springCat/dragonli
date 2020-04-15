package org.springcat.dragonli.jfinal;

import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import org.springcat.dragonli.consul.ConsulUtil;


public class JFinalStatusController extends Controller {

    public void index(){
        renderText("ok");
    }

    public void info(){
        renderJson(ConsulUtil.getAppInfo());
    }

    public static void init(Routes me){
        me.add("/status", JFinalStatusController.class);
    }
}
