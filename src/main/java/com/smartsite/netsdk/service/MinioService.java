package com.smartsite.netsdk.service;

import com.smartsite.netsdk.common.ResultBean;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 11:57
 */
public interface MinioService {

    /**
     * 文件上传
     */
    ResultBean uploadFile(MultipartFile file);

    /**
     * 链接获取
     */
    ResultBean preSigned(String fileName);

    /**
     * 删除文件
     * @param fileName f
     * @return d
     */
    ResultBean deleteFile(String fileName);
}
