package com.smartsite.netsdk.bean;

import lombok.AllArgsConstructor;

/**
 * 人脸闸机设别
 */
@lombok.Data
@AllArgsConstructor
public class DeviceDto {
    private String id;
    private String m_strIp;
    private Integer m_nPort;
    private String m_strUser;
    private String m_strPassword;
}
