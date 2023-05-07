package com.smartsite.netsdk.bean;

import lombok.Data;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 8:58
 */
@Data
public class CardDto {

    /**
     * 序号
     */
    private String serialNumber;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡号
     */
    private String userId;

    /**
     * userId
     */
    private String cardName;

    /**
     * "卡密码
     */
    private String cardPasswd;

    /**
     * 卡状态[-1:未知|0:正常|1:挂失|2:注销|3:冻结|4:欠费|5:逾期|6:预欠费]
     */
    private Integer cardStatus;

    /**
     * 卡状态名称
     */
    private String cardStatusName;

    /**
     * 卡类型[-1:未知|0:一般卡|1:VIP卡|2:来宾卡|3:巡逻卡|4:黑名单卡|5:胁迫卡|6:巡检卡|7:母卡]
     */
    private Integer cardType;

    /**
     * 卡类型名称
     */
    private String cardTypeName;

    /**
     * 使用次数，仅当卡类型为来宾卡时有效
     */
    private String useTimes;

    /**
     * 是否首卡[0:不是|1:是]
     */
    private Integer firstEnter;

    /**
     * 是否有效[0:不是|1:是]
     */
    private Integer enable;

    /**
     * 有效期开始时间
     */
    private String startTime;

    /**
     * 有效期结束时间
     */
    private String endTime;

    /**
     * 记录集编号， 修改、删除卡信息必须填写
     */
    private Integer recordNo;

    /**
     * 人脸照片Url
     */
    private String faceUrl;

    /**
     * 工种
     */
    private String typeOfWork;

    /**
     * 分包
     */
    private String subpackage;

    /**
     * 人脸图片路径
     */
    private String szPhotoPath;

    /**
     * 单位名称
     */
    private String szCompanyName;

    /**
     * 证件地址
     */
    private String szCitizenAddress;

    /**
     * 施工单位全称
     */
    private String szBuilderName;

    /**
     * 项目名称
     */
    private String szProjectName;

    /**
     * 施工单位类型
     */
    private String szBuilderType;

    /**
     * 人脸数据
     */
    private String szFaceData;
}
