package com.pjh.openApi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pjh.interfac.model.UserInterfaceInfo;

/**
* @author 宇宙无敌超级大帅哥
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-01-19 15:53:16
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 调用一次接口，消耗一次调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    Boolean useOneChance(Long userId,Long interfaceInfoId);
}
