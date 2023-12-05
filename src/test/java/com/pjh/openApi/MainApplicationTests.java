package com.pjh.openApi;

import com.pjh.openApi.config.WxOpenConfig;

import javax.annotation.Resource;

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

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }

    @Test
    void Text(){
        String originalUrl = "https://api.paugram.com/wallpaper/";

        // 发起 HTTP 请求
        HttpResponse response = HttpRequest.get(originalUrl).execute();

        // 获取重定向后的 URL
        String redirectedUrl = response.header("Location");
        System.out.println("Redirected URL: " + redirectedUrl);

    }

}
