package org.springcat.dragonli.jfinal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.jfinal.config.Routes;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import org.springcat.dragonli.core.registry.AppConf;

import java.util.List;


public class JFinalStatusController extends Controller {

    private final static Log log = LogFactory.get();

    private static Routes routes;
    private static ActionMapping actionMapping;
    private static AppConf appConf;
    private static List<String> urlList;

    public void index(){
        renderText("ok");
    }

    protected static void init(Routes me, AppConf appConfPara){
        me.add("/status", JFinalStatusController.class);
        routes = me;
        appConf = appConfPara;
        actionMapping = new ActionMapping(me);
        ReflectUtil.invoke(actionMapping,"buildActionMapping");
        urlList = getUrls(appConfPara,actionMapping);

        log.info("init service url :{}"+urlList);
    }

    public void appconf(){
        renderJson(appConf);
    }

    public void urls(){
        renderJson(urlList);
    }

    private static List<String> getUrls(AppConf appConf, ActionMapping actionMapping){
        String rootPath = appConf.getRootPath();
        List<String> allActionKeys = actionMapping.getAllActionKeys();
        List<String> urls = CollectionUtil.newArrayList();
        for (String allActionKey : allActionKeys) {
            urls.add(rootPath + allActionKey);
        }
        return urls;
    }
}
