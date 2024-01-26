package com.pjh.pjhapiclientsdk.client;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.pjh.pjhapiclientsdk.model.User;
import com.pjh.pjhapiclientsdk.utils.SignUtils;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class Client {

    private String accessKey;

    private String secretKey;

    private static final String GATEWAY_HOST = "http://localhost:8105";

    public Map<String, String> getStringRequestHeader() {
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("sign", SignUtils.getSign(accessKey, secretKey));
        map.put("nonce", RandomUtil.randomNumbers(5));
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        return map;
    }

    public String send(String method, String url) {
        String result = null;
        if (url.startsWith("/api")) {
            url = GATEWAY_HOST + url;
        }
        switch (method) {
            case "GET":
                HttpResponse response = HttpRequest.get(url).addHeaders(getStringRequestHeader()).execute();
                if (response.getStatus() == 302) {
                    HttpResponse newResponse = HttpRequest.get(url).execute();
                    // 获取重定向后的 URL,这里我提前知道是图片，返回重定向url就可以了
                    String redirectedUrl = newResponse.header("Location");
                    result = redirectedUrl;
                } else {
                    result = response.body();
                }
                break;
            case "POST":
                result = HttpRequest.post(url)
                        .addHeaders(getStringRequestHeader())
                        .execute().body();
        }
        System.out.println(result);
        return result;
    }

}
