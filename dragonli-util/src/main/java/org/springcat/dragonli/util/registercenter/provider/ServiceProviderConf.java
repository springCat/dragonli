package org.springcat.dragonli.util.registercenter.provider;

import lombok.Data;
import org.springcat.dragonli.util.config.IConfig;

/**
 * @Description ServiceConsumerConf
 * @Author springCat
 * @Date 2020/5/6 15:20
 */
@Data
public class ServiceProviderConf implements IConfig {

    //unit : second
    private Integer serviceScanPeriod = 3;

    private Integer serviceFetcherNum = 5;

}
