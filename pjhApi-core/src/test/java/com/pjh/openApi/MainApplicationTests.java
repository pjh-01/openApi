package com.pjh.openApi;

import com.pjh.openApi.config.WxOpenConfig;

import javax.annotation.Resource;

import com.pjh.openApi.service.UserInterfaceInfoService;
import com.pjh.pjhapiclientsdk.client.Client;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Resource
    private Client client;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    void contextLoads() {
        userInterfaceInfoService.useOneChance(1726904964312272897L,3L);
    }


}
