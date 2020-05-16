package org.springcat.dragonli.core.config;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description SettingUtilTest
 *   @Author springCat
 * @Date 2020/4/30 12:04
 */
public class SettingUtilTest {

    @Data
    static class GroupNameConf implements IConfig {
        private Integer id;
        private String name;
        private int[] labels;
    }

    @Test
    public void tesGet(){
        GroupNameConf groupNameConf = new GroupNameConf();
        groupNameConf= groupNameConf.load();
        Assert.assertTrue(groupNameConf.getId() == 1);
        Assert.assertEquals(groupNameConf.getName(),"name");
        int[] except = {1,2,3};
        Assert.assertArrayEquals(groupNameConf.getLabels(),except);
    }
}
