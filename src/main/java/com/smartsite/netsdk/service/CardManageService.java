package com.smartsite.netsdk.service;

import com.smartsite.netsdk.bean.CardDto;
import com.smartsite.netsdk.common.ResultBean;

import java.io.IOException;
import java.util.List;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 16:01
 */
public interface CardManageService {

    ResultBean add(CardDto cardDto) throws IOException;

    ResultBean update(CardDto cardDto) throws IOException;

    ResultBean delete( String recordNo,String userId);

    /**
     * 获取卡列表
     * @return r
     */
    ResultBean getList();

    /**
     * 获取卡列表
     * @return r
     */
    List<CardDto> getListForBody();
}
