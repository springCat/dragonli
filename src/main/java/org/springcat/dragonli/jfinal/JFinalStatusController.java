package org.springcat.dragonli.jfinal;

import com.jfinal.config.Routes;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import org.springcat.dragonli.core.registry.AppConf;


public class JFinalStatusController extends Controller {

    private static Routes routes;
    private static ActionMapping actionMapping;
    private static AppConf appConf;

    public void index(){
        renderText("ok");
    }

    public static void init(Routes me, AppConf appConfPara){
        me.add("/status", JFinalStatusController.class);
        routes = me;
        appConf = appConfPara;
//        actionMapping = new ActionMapping(me);
//        ReflectUtil.invoke(actionMapping,"buildActionMapping");
    }

    public void appconf(){
        renderJson(appConf);
    }

//    public void urls(){
//        String rootPath = appConf.getRootPath();
//        List<String> allActionKeys = actionMapping.getAllActionKeys();
//        List<String> url = CollectionUtil.newArrayList();
//        for (String allActionKey : allActionKeys) {
//            url.add(rootPath + allActionKey);
//        }
//        renderJson(url);
//    }
}
