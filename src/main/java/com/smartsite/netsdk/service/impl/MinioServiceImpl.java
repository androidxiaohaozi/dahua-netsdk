package com.smartsite.netsdk.service.impl;

import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 11:57
 */
@Service
public class MinioServiceImpl implements MinioService {

    @Resource
    MinioClient minioClient;

    @Value("${minio.addBucket}")
    private String addBucket;

    @Value("${minio.officalUrl}")
    private String officalUrl;

    /**
     * 文件上传
     */
    @Override
    public ResultBean uploadFile(MultipartFile file) {
        //1.上传图片到minio服务器并返回地址
        if (file == null) {
            return ResultBean.error("文件不能为空");
        }
        if (file.getSize() < 1) {
            return ResultBean.error("文件内容不能为空");
        }
        String uploadImageName = getUploadImageName(file);
        try {
            InputStream in = file.getInputStream();
            PutObjectOptions putObjectOptions = new PutObjectOptions(in.available(), -1);
            String contentType = file.getContentType();
            if (contentType == null){
                return ResultBean.error("文件格式错误");
            }
            putObjectOptions.setContentType(contentType);
            minioClient.putObject(addBucket, uploadImageName, in, putObjectOptions);
            //下载地址
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(uploadImageName)) {
            return ResultBean.error("上传失败");
        }
        //封装返回前台的参数
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("fileName", uploadImageName);
        String fileUrl = null;
        ResultBean resultBean = preSigned(uploadImageName);
        Object object = resultBean.get(ResultBean.DATA_TAG);
        if (!(Integer.parseInt(resultBean.get(ResultBean.CODE_TAG).toString())== ResultBean.FAIL || object == null)) {
            fileUrl = object.toString();
        }
        returnMap.put("fileUrl", fileUrl);
        //文件名
        return ResultBean.success(returnMap);
    }

    /**
     * 链接获取
     */
    @Override
    public ResultBean preSigned(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return ResultBean.error("文件名称不能为空");
        }
        String url = "";
        try {
            url = minioClient.presignedGetObject(addBucket, fileName);
            url = url.substring(StringUtils.ordinalIndexOf(url, "/", 3));
            url = officalUrl + url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.success((Object)url);
    }

    /**
     * 删除文件
     * @param fileName f
     * @return r
     */
    @Override
    public ResultBean deleteFile(String fileName){
        if (StringUtils.isEmpty(fileName)) {
            return ResultBean.error("文件名称不能为空");
        }
        try {
            minioClient.removeObject(addBucket, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.success();
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static String getUploadImageName(MultipartFile file) {
        // 获取文件前缀
        int index = file.getOriginalFilename().indexOf(".") + 1;
        // 文件格式
        String prefix = file.getOriginalFilename().substring(index);
        // 文件名
        String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        // 生成6位数随机数
        String random = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        return name + sdf.format(new Date()) + random + "." + prefix;
    }
}
