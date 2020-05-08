package org.springcat.dragonli.jfinal;

import cn.hutool.core.util.StrUtil;
import com.jfinal.core.Controller;
import org.springcat.dragonli.util.configcenter.ConfigCenter;

/**
 * 暴露配置管理接口,后续可以获取consul中所有的服务器,然后依次调用刷新接口,来控制集群配置更新
 */
public class ConfigController extends Controller {

    public void sysConf(){
        renderJson(ConfigCenter.me().getSysConf());
    }

    public void userConf(){
        String key = getPara("k");
        if(StrUtil.isNotBlank(key)){
            renderJson(ConfigCenter.me().getUserConf().get(key));
            return;
        }
        renderJson(ConfigCenter.me().getUserConf());
    }

    public void refreshUserConf(){
        ConfigCenter.me().refreshUserConf();
        renderText("ok");
    }

    public void fetchUserConf(){
        String key = getPara("k");
        ConfigCenter.me().pullUserConf(key);
        renderText("ok");
    }

    public void setUserConf(){
        String key = getPara("k");
        String value = getPara("v");
        ConfigCenter.me().setUserConf(key,value);
        renderText("ok");
    }
}
