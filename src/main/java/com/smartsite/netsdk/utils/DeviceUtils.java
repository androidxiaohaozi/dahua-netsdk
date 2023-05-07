package com.smartsite.netsdk.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.smartsite.netsdk.demo.module.LoginModule;
import com.smartsite.netsdk.bean.DeviceDto;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 设备工具类
 * (暂时使用静态数据，实际使用时需要使用数据库中的数据)
 *
 * @author Yan Xu
 * @version 1.0
 * @date 2021/8/6
 * Copyright © goodits
 */
@Slf4j
public class DeviceUtils {

    private static final Map<String, DeviceDto> DEVICE_DTO_MAP = new LinkedHashMap<>(16);


    static {
        DeviceDto deviceDto = new DeviceDto("1", "192.168.1.247", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto.getId(), deviceDto);
        DeviceDto deviceDto1 = new DeviceDto("2", "192.168.1.246", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto1.getId(), deviceDto1);
    }

    public static boolean login(String deviceNo) {
        DeviceDto deviceDto = DEVICE_DTO_MAP.get(deviceNo);
        if (ObjectUtil.isEmpty(deviceDto)) {
            throw new ServiceException(StrUtil.format("不存在设备：{}", deviceNo));
        }
        return LoginModule.login(deviceDto.getM_strIp(), deviceDto.getM_nPort(), deviceDto.getM_strUser(), deviceDto.getM_strPassword());
    }

    public static void logout() {
        LoginModule.logout();
    }
}
