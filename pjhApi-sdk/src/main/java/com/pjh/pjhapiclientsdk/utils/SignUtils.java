package com.pjh.pjhapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class SignUtils {

    public static String getSign(String accessKey,String secretKey){
        return DigestUtil.md5Hex(accessKey + secretKey);
    }
}
