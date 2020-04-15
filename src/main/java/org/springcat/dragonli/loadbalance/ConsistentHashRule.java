package org.springcat.dragonli.loadbalance;

import cn.hutool.core.util.HashUtil;
import com.ecwid.consul.v1.health.model.HealthService;
import java.util.List;

public class ConsistentHashRule implements ILoadBalanceRule{


    public HealthService choose(List<HealthService> serviceList,byte[] loadBalanceParam) {
        if(serviceList == null || serviceList.size() == 0){
            return null;
        }
        int i = consistentHash(HashUtil.murmur32(loadBalanceParam), serviceList.size());
        return serviceList.get(i);
    }

    public  int consistentHash(long input, int buckets) {
        input = 2862933555777941757L * input + 1;
        double nextDouble = ((double) ((int) (input >>> 33) + 1)) / (0x1.0p31);
        // Jump from bucket to bucket until we go out of range
        int candidate = 0;
        int next;
        while (true) {
            next = (int) ((candidate + 1) / nextDouble);
            if (next >= 0 && next < buckets) {
                candidate = next;
            } else {
                return candidate;
            }
        }
    }
}
