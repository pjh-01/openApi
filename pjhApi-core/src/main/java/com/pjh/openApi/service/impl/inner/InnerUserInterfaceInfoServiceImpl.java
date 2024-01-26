package com.pjh.openApi.service.impl.inner;

import com.pjh.interfac.InnerUserInterfaceInfoService;
import com.pjh.openApi.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    public Boolean useOneChance(Long userId, Long interfaceInfoId) {
        return userInterfaceInfoService.useOneChance(userId, interfaceInfoId);
    }
}
