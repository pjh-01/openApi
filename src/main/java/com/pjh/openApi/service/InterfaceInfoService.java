package com.pjh.openApi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjh.openApi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.pjh.openApi.model.dto.post.PostQueryRequest;
import com.pjh.openApi.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pjh.openApi.model.entity.Post;

/**
* @author 宇宙无敌超级大帅哥
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-11-21 18:24:35
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验接口信息
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);


    /**
     * 根据用户id获取接口
     *
     * @param userId
     * @return
     */
    Page<InterfaceInfo> getListByUserId(Long userId);

    /**
     * 获取查询条件
     *
     * @param
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest queryRequest);


}
