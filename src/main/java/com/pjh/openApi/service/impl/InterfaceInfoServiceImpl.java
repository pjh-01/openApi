package com.pjh.openApi.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pjh.openApi.common.ErrorCode;
import com.pjh.openApi.common.PageRequest;
import com.pjh.openApi.constant.CommonConstant;
import com.pjh.openApi.exception.BusinessException;
import com.pjh.openApi.exception.ThrowUtils;
import com.pjh.openApi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.pjh.openApi.model.entity.InterfaceInfo;
import com.pjh.openApi.model.entity.Post;
import com.pjh.openApi.service.InterfaceInfoService;
import com.pjh.openApi.mapper.InterfaceInfoMapper;
import com.pjh.openApi.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 宇宙无敌超级大帅哥
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-11-21 18:24:35
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    @Override
    public Page<InterfaceInfo> getListByUserId(Long userId) {

        PageRequest pageRequest = new PageRequest();
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<InterfaceInfo> wrapper=new QueryWrapper<>();
        wrapper.eq("userId",userId);
        return this.page(new Page<>(current, size),wrapper);

    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest queryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return queryWrapper;
        }
        Long id = queryRequest.getId();
        String name = queryRequest.getName();
        String description = queryRequest.getDescription();
        String url = queryRequest.getUrl();
        String requestHeader = queryRequest.getRequestHeader();
        String responseHeader = queryRequest.getResponseHeader();
        Integer status = queryRequest.getStatus();
        String method = queryRequest.getMethod();
        Long userId = queryRequest.getUserId();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        // 拼接查询条件-String部分
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.like(StringUtils.isNotBlank(requestHeader), "requestHeader", requestHeader);
        queryWrapper.like(StringUtils.isNotBlank(responseHeader), "responseHeader", responseHeader);
        queryWrapper.like(StringUtils.isNotBlank(method), "method", method);
        // 拼接查询条件-int部分
        queryWrapper.eq(ObjectUtils.isNotEmpty(id)&&id>0, "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status)&&(status==0||status==1), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId)&&userId>0, "userId", userId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


}




