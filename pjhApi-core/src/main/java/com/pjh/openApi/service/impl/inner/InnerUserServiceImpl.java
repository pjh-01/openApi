package com.pjh.openApi.service.impl.inner;

import com.pjh.interfac.InnerUserService;
import com.pjh.interfac.model.User;
import com.pjh.openApi.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public User getInvokerUser(String accessKey) {
        return userService.getInvokerUser(accessKey);
    }
}
