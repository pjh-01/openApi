package com.pjh.openApi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.pjh.openApi.common.ErrorCode;
import com.pjh.openApi.exception.BusinessException;
import com.pjh.openApi.mapper.UserInterfaceInfoMapper;
import com.pjh.interfac.model.UserInterfaceInfo;
import com.pjh.openApi.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 * @author 宇宙无敌超级大帅哥
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2024-01-19 15:53:16
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService{

    @Override
    public Boolean useOneChance(Long userId, Long interfaceInfoId) {
        // 判断合法
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.update()
                .setSql("totalNum=totalNum+1,leftNum=leftNum-1")
                .eq("userId", userId)
                .eq("interfaceInfoId", interfaceInfoId)
                .gt("leftNum",0)
                .update();
    }
}




