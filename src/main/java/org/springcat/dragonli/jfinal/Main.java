package org.springcat.dragonli.jfinal;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.setting.Setting;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Main {



    public static void main(String[] args) {



        Setting setting = new Setting("dragonli.setting");
        setting.autoLoad(true);

        ConsulConf consulConfBean = new ConsulConf();
        Setting consulConf = null;

        while (true) {
            consulConf = setting.getSetting("consul");
            consulConfBean = consulConf.toBean(consulConfBean);
            System.out.println(consulConfBean);

            setting.clear();
            consulConf = setting.getSetting("db");
            consulConfBean = consulConf.toBean(consulConfBean);
            System.out.println(consulConfBean);

            ThreadUtil.sleep(1000);
        }



    }




    @Data
    static class ConsulConf{
        private String ip;
        private int port;
    }
}
