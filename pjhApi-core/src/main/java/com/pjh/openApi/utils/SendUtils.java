package com.pjh.openApi.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class SendUtils {
    public static String send(String method, String url){
        String result = null;
        switch (method){
            case "GET":
                HttpResponse response = HttpRequest.get(url).execute();
                if(response.getStatus()==302){
                    HttpResponse newResponse = HttpRequest.get(url).execute();
                    // 获取重定向后的 URL,这里我提前知道是图片，返回重定向url就可以了
                    String redirectedUrl = newResponse.header("Location");
                    result=redirectedUrl;
                }else {
                    result=response.body();
                }
                break;
            case "POST":
                result = HttpRequest.post(url)
                        .execute().body();
        }
        System.out.println(result);
        return result;
    }
}
