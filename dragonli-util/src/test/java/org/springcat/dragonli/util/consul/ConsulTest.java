package org.springcat.dragonli.util.consul;

import cn.hutool.setting.Setting;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @Description ConsulTest
 * @Author springCat
 * @Date 2020/4/30 17:57
 */

public class ConsulTest {

    @Test
    public void getKVValuesSuccess() {
        String k = "key";
        String v = "value";

        GetValue getValue = mockGetValue(k, v);
        List<GetValue> list = Arrays.asList(getValue);
        Response<List<GetValue>> resp = new Response<List<GetValue>>(list,1L, false, 1L);
        ConsulClient consulClient = initMockClientForKVValues(resp);

        Consul consul = new Consul();
        consul.setClient(consulClient);
        Setting kvValues = consul.getKVValues(k);
        Assert.assertEquals(v,kvValues.get(""));
    }

    @Test
    public void getKVValuesError() {
        String k = "key";
        String v = "value";
        Consul consul = new Consul();
        Setting kvValues = consul.getKVValues(k);
        Assert.assertTrue(kvValues.isEmpty());
    }

    @Test
    public void getKVValueSuccess() {
        String k = "key";
        String v = "value";

        GetValue getValue = mockGetValue(k, v);
        Response<GetValue> resp = new Response<GetValue>(getValue,1L, false, 1L);
        ConsulClient consulClient = initMockClientForKVValue(resp);

        Consul consul = new Consul();
        consul.setClient(consulClient);
        String kvValue = consul.getKVValue(k);
        Assert.assertEquals(v,kvValue);
    }

    @Test
    public void getKVValueError() {
        String k = "key";

        Consul consul = new Consul();

        String kvValue = consul.getKVValue(k);
        Assert.assertTrue("".equals(kvValue));
    }

    private GetValue mockGetValue(String key, String value){
        GetValue getValue = new GetValue();
        getValue.setKey(key);
        getValue.setValue(Base64.getEncoder().encodeToString(value.getBytes(Charset.forName("UTF-8"))));
        return getValue;
    }

    private ConsulClient initMockClientForKVValues(Response<List<GetValue>> resp){
        ConsulClient mockConsulClient = Mockito.mock(ConsulClient.class);
        Mockito.when(mockConsulClient.getKVValues(Mockito.anyString())).thenReturn(resp);
        return mockConsulClient;
    }

    private ConsulClient initMockClientForKVValue(Response<GetValue> resp){
        ConsulClient mockConsulClient = Mockito.mock(ConsulClient.class);
        Mockito.when(mockConsulClient.getKVValue(Mockito.anyString())).thenReturn(resp);
        return mockConsulClient;
    }
}
