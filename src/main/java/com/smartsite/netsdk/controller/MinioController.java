package com.smartsite.netsdk.controller;

import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.service.MinioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 13:10
 */
@RestController
@RequestMapping("/minio")
public class MinioController {

    @Resource
    MinioService minioService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public ResultBean uploadInvoice(MultipartFile file) {
        return minioService.uploadFile(file);
    }

    /**
     * 文件地址获取
     */
    @GetMapping("/preSigned")
    public ResultBean preSigned(String fileName) {
        return minioService.preSigned(fileName);
    }
}
