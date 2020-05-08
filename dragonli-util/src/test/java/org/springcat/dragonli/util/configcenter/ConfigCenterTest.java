//package org.springcat.dragonli.util.configcenter;
//
//import cn.hutool.setting.Setting;
//import org.junit.Assert;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.springcat.dragonli.util.consul.Consul;
//
///**
// * @Description ConfigCenterTest
// * @Author springCat
// * @Date 2020/4/30 15:39
// */
//
//public class ConfigCenterTest {
//
//    private ConfigCenterConf configCenterConf = new ConfigCenterConf().load();
//
//
//    private void getConfigCenterForList(String key, Setting value){
//        Consul mock = Mockito.mock(Consul.class);
//        Mockito.when(mock.getKVValues(key)).thenReturn(value);
//        ConfigCenter.init(configCenterConf,mock);
//    }
//
//    private ConfigCenter getConfigCenterForListInit(String confPath){
//        String key = "key";
//        Setting value = new Setting();
//
//        value.put(key,"value");
//        return getConfigCenterForList(confPath,value);
//    }
//
//    private ConfigCenter getConfigCenter(String key, String value){
//        Consul mock = Mockito.mock(Consul.class);
//        Mockito.when(mock.getKVValue(key)).thenReturn(value);
//        return new ConfigCenter(configCenterConf,mock);
//    }
//
//    private ConfigCenter getConfigCenterInit(){
//        String key = "key";
//        String value = "value";
//        return getConfigCenter(configCenterConf.getUserConfPath()+configCenterConf.getApplicationName()+key,value);
//    }
//
//    @Test
//    public void refreshBootStrapConf() {
//        ConfigCenter configCenter = getConfigCenterForListInit(configCenterConf.getBootstrapConfPath()+configCenterConf.getApplicationName());
//        configCenter.refreshBootStrapConf();
//        Setting result = configCenter.getBootStrapConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//
//    @Test
//    public void pullSysConf() {
//        ConfigCenter configCenter = getConfigCenterForListInit(configCenterConf.getSysConfPath());
//        Setting result = configCenter.pullSysConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//
//    @Test
//    public void refreshSysConf() {
//        ConfigCenter configCenter = getConfigCenterForListInit(configCenterConf.getSysConfPath());
//        configCenter.refreshSysConf();
//        Setting result = configCenter.getSysConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//
//    @Test
//    public void pullUserConf() {
//        ConfigCenter configCenter = getConfigCenterForListInit(configCenterConf.getUserConfPath()+configCenterConf.getApplicationName());
//        Setting result = configCenter.pullUserConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//
//    @Test
//    public void refreshUserConf() {
//        ConfigCenter configCenter = getConfigCenterForListInit(configCenterConf.getUserConfPath()+configCenterConf.getApplicationName());
//        configCenter.refreshUserConf();
//        Setting result = configCenter.getUserConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//
//    @Test
//    public void pullUserConfSingle() {
//        ConfigCenter configCenter = getConfigCenterInit();
//        String result = configCenter.pullUserConf("key");
//        Assert.assertEquals("value",result);
//    }
//
//    @Test
//    public void refreshUserConfSingle() {
//        ConfigCenter configCenter = getConfigCenterInit();
//        configCenter.refreshUserConf("key");
//        Setting result = configCenter.getUserConf();
//        Assert.assertEquals("value",result.get("key"));
//    }
//}
