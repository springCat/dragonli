package org.springcat.dragonli.jfinal;

import cn.hutool.core.util.StrUtil;
import com.jfinal.core.Controller;
import org.springcat.dragonli.core.config.ConfigUtil;

/**
 * 暴露配置管理接口,后续可以获取consul中所有的服务器,然后依次调用刷新接口,来控制集群配置更新
 */
public class ConfigController extends Controller {

    public void sysConf(){
        renderJson(ConfigUtil.getSysConf());
    }

    public void userConf(){
        String key = getPara("k");
        if(StrUtil.isNotBlank(key)){
            renderJson(ConfigUtil.getUserConf().get(key));
            return;
        }
        renderJson(ConfigUtil.getUserConf());
    }

    public void refreshUserConf(){
        ConfigUtil.refreshUserConf();
        renderText("ok");
    }

    public void fetchUserConf(){
        String key = getPara("k");
        ConfigUtil.pullUserConf(key);
        renderText("ok");
    }

    public void setUserConf(){
        String key = getPara("k");
        String value = getPara("v");
        ConfigUtil.setUserConf(key,value);
        renderText("ok");
    }
}
