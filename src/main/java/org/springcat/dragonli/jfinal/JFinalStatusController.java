package org.springcat.dragonli.jfinal;

import com.jfinal.config.Routes;
import com.jfinal.core.Controller;


public class JFinalStatusController extends Controller {

    public void index(){
        renderText("ok");
    }

    public static void init(Routes me){
        me.add("/status", JFinalStatusController.class);
    }
}
