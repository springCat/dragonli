package org.springcat.dragonli.core.jfinal.health;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.jfinal.config.Routes;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import org.springcat.dragonli.core.registercenter.register.ApplicationConf;

import java.util.List;


public class JFinalStatusController extends Controller {

    private final static Log log = LogFactory.get();

    private static Routes routes;
    private static ActionMapping actionMapping;
    private static ApplicationConf appConf;
    private static List<String> urlList;

    public void index(){
        renderText("ok");
    }

    public static void init(Routes me, ApplicationConf appConfPara){
        me.add("/status", JFinalStatusController.class);
        routes = me;
        appConf = appConfPara;
        actionMapping = new ActionMapping(me);
        ReflectUtil.invoke(actionMapping,"buildActionMapping");
        urlList = getUrls(appConfPara,actionMapping);

        log.info("init service url :{}"+urlList);
    }

    public void appInfo(){
        renderJson(appConf);
    }

    public void urls(){
        renderJson(urlList);
    }


    private static List<String> getUrls(ApplicationConf appConf, ActionMapping actionMapping){
        String rootPath = appConf.getRootPath();
        List<String> allActionKeys = actionMapping.getAllActionKeys();
        List<String> urls = CollectionUtil.newArrayList();
        for (String allActionKey : allActionKeys) {
            urls.add(rootPath + allActionKey);
        }
        return urls;
    }
}
