package com.pjh.openApi.service.impl.inner;


import com.pjh.interfac.InnerInterfaceInfoService;
import com.pjh.interfac.model.InterfaceInfo;
import com.pjh.openApi.service.InterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Override
    public InterfaceInfo isExist(String url, String method) {
        return interfaceInfoService.isExist(url,method);
    }
}
