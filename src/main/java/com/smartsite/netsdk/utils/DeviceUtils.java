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
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/27 16:15
 */
@Slf4j
public class DeviceUtils {

    private static final Map<String, DeviceDto> DEVICE_DTO_MAP = new LinkedHashMap<>(16);


    static {
        DeviceDto deviceDto = new DeviceDto("1", "192.168.1.247", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto.getId(), deviceDto);
        DeviceDto deviceDto1 = new DeviceDto("2", "192.168.1.246", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto1.getId(), deviceDto1);

        DeviceDto deviceDto2 = new DeviceDto("3", "192.168.1.17", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto2.getId(), deviceDto2);
        DeviceDto deviceDto3 = new DeviceDto("4", "192.168.1.26", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto3.getId(), deviceDto3);
        DeviceDto deviceDto4 = new DeviceDto("5", "192.168.1.28", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto4.getId(), deviceDto4);
        DeviceDto deviceDto5 = new DeviceDto("6", "192.168.1.29", 37777, "admin", "a1234567");
        DEVICE_DTO_MAP.put(deviceDto5.getId(), deviceDto5);
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
