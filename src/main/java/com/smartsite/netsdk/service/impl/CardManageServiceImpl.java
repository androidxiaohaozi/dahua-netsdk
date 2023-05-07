package com.smartsite.netsdk.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smartsite.netsdk.bean.CardDto;
import com.smartsite.netsdk.common.Res;
import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.demo.module.GateModule;
import com.smartsite.netsdk.lib.NetSDKLib;
import com.smartsite.netsdk.lib.ToolKits;
import com.smartsite.netsdk.service.CardManageService;
import com.smartsite.netsdk.service.MinioService;
import com.smartsite.netsdk.utils.HttpClientUtil;
import com.smartsite.netsdk.utils.ServiceException;
import com.sun.jna.Memory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 16:02
 */
@Service
public class CardManageServiceImpl implements CardManageService {

    /**
     * 新增
     */
    @Value("${cardManageAddUrl}")
    private String cardManageAddUrl;

    /**
     * 修改
     */
    @Value("${cardManageUpdateUrl}")
    private String cardManageUpdateUrl;

    /**
     * 删除
     */
    @Value("${cardManageDeleteUrl}")
    private String cardManageDeleteUrl;

    /**
     * 添加卡
     * @param cardDto c
     * @return c
     */
    @Override
    public ResultBean add(CardDto cardDto) throws IOException {

        String faceUrl = cardDto.getFaceUrl();
        Memory memory = ToolKits.readPictureFile(faceUrl);
        /**
         * 先校验
         */
        checkCardDtoAndMemory(cardDto,memory);
        // 先添加卡，卡添加成功后，再添加图片
        int useTimes;
        if (StrUtil.isBlank(cardDto.getUseTimes())) {
            useTimes = 0;
        } else {
            useTimes = Integer.parseInt(cardDto.getUseTimes());
        }

        boolean bCardFlags = GateModule.insertCard(cardDto.getCardNo(), cardDto.getUserId(), cardDto.getCardName(),
                cardDto.getCardPasswd(), cardDto.getCardStatus(),
                cardDto.getCardType(), useTimes,
                cardDto.getFirstEnter(), cardDto.getEnable(), cardDto.getStartTime(), cardDto.getEndTime());
        String cardError = "";
        if (!bCardFlags) {
            cardError = ToolKits.getErrorCodeShow();
        }

        boolean bFaceFalgs = GateModule.addFaceInfo(cardDto.getUserId(), memory);
        String faceError = "";
        if (!bFaceFalgs) {
            faceError = ToolKits.getErrorCodeShow();
        }
        // 添加卡信息和人脸成功
        if (bCardFlags && bFaceFalgs) {
            return ResultBean.success(Res.string().getSucceedAddCardAndPerson());
        }
        // 添加卡信息和人脸失败
        if (!bCardFlags && !bFaceFalgs) {
            return ResultBean.error("添加卡信息和人脸失败:" + cardError);
        }
        // 添加卡信息成功，添加人脸失败
        if (bCardFlags) {
            return ResultBean.error("添加卡信息成功，添加人脸失败:" + faceError);
        }

        //都成功以后把信息同步到系统
        JSONObject jsonObject = convertBody(cardDto);
        JSONObject jsonObject1 = HttpClientUtil.httpPost(cardManageAddUrl, jsonObject);
        if (jsonObject1.get("code") != null) {
            Object code = jsonObject1.get("code");
            int i = Integer.parseInt(code.toString());
            if (i == 200) {
                return ResultBean.success("新增成功！！！");
            } else{
                return ResultBean.error("卡信息都添加成功，同步系统数据失败，请稍后再试！！！");
            }
        } else {
            return ResultBean.error("卡信息都添加成功，同步系统数据失败，请稍后再试！！！");
        }

    }

    /**
     * 修改卡
     * @param cardDto c
     * @return r
     */
    @Override
    public ResultBean update(CardDto cardDto) throws IOException {
        String faceUrl = cardDto.getFaceUrl();
        Memory memory = ToolKits.readPictureFile(faceUrl);
        checkCardDtoAndMemory(cardDto, memory);
        // 先修改卡，卡修改成功后，再修改图片
        int useTimes;
        if (StrUtil.isBlank(cardDto.getUseTimes())) {
            useTimes = 0;
        } else {
            useTimes = Integer.parseInt(cardDto.getUseTimes());
        }

        boolean bCardFlags = GateModule.modifyCard(cardDto.getRecordNo(), cardDto.getCardNo(),
                cardDto.getUserId(), cardDto.getCardName(),
                cardDto.getCardPasswd(), cardDto.getCardStatus(),
                cardDto.getCardType(), useTimes,
                cardDto.getFirstEnter(), cardDto.getEnable(), cardDto.getStartTime(), cardDto.getEndTime());
        if (Boolean.FALSE.equals(bCardFlags)) {
            throw new ServiceException(Res.string().getFailedModifyCard() + " : " + ToolKits.getErrorCodeShow());
        }
        boolean bFaceFalgs = GateModule.modifyFaceInfo(cardDto.getUserId(), memory);
        if (Boolean.FALSE.equals(bFaceFalgs)) {
            throw new ServiceException(Res.string().getSucceedModifyCardButFailedModifyPerson() + " : " + ToolKits.getErrorCodeShow());
        }

        //都修改完成以后同步系统数据
        JSONObject jsonObject = convertBody(cardDto);
        JSONObject jsonObject1 = HttpClientUtil.httpPost(cardManageUpdateUrl, jsonObject);

        if (jsonObject1.get("code") != null) {
            Object code = jsonObject1.get("code");
            int i = Integer.parseInt(code.toString());
            if (i == 200) {
                return ResultBean.success("修改成功！！！");
            } else{
                return ResultBean.error("卡信息都修改成功，同步系统数据失败，请稍后再试！！！");
            }
        } else {
            return ResultBean.error("卡信息都修改成功，同步系统数据失败，请稍后再试！！！");
        }
    }

    /**
     * 删除卡
     * @param userId u
     * @return r
     */
    @Override
    public ResultBean delete( String recordNo,String userId) {

        boolean deleteFaceInfo = GateModule.deleteFaceInfo(userId);
        boolean deleteCard = GateModule.deleteCard(Integer.parseInt(recordNo));
        // 删除人脸和卡信息
        if (deleteFaceInfo && deleteCard) {
            return ResultBean.success(Res.string().getSucceed());
        }
        throw new ServiceException(ToolKits.getErrorCodeShow());
    }

    /**
     * 获取卡列表
     * @return r
     */
    @Override
    public List<CardDto> getListForBody() {
        List<CardDto> cardDtoList = new LinkedList<>();
        // 卡号：  为空，查询所有的卡信息
        // 获取查询句柄
        if (!GateModule.findCard("")) {
            return cardDtoList;
        }
        try {
            int count = 0;
            int index;
            int nFindCount = 10;

            // 查询具体信息
            while (true) {
                NetSDKLib.NET_RECORDSET_ACCESS_CTL_CARD[] pstRecord = GateModule.findNextCard(nFindCount);
                if (pstRecord == null) {
                    break;
                }

                for (int i = 0; i < pstRecord.length; i++) {
                    index = i + count * nFindCount;

                    CardDto cardDto = new CardDto();

                    // 序号
                    cardDto.setSerialNumber(String.valueOf(index + 1));
                    // 卡号
                    cardDto.setCardNo(new String(pstRecord[i].szCardNo).trim());
                    // 卡名
                    cardDto.setCardName(new String(pstRecord[i].szCardName, StandardCharsets.UTF_8).trim());
                    // 用户ID
                    cardDto.setUserId(new String(pstRecord[i].szUserID).trim());
                    // 卡密码
                    cardDto.setCardPasswd(new String(pstRecord[i].szPsw).trim());
                    // 卡状态
                    cardDto.setCardStatus(pstRecord[i].emStatus);
                    cardDto.setCardStatusName(Res.string().getCardStatus(pstRecord[i].emStatus));
                    // 卡类型
                    cardDto.setCardType(pstRecord[i].emType);
                    cardDto.setCardTypeName(Res.string().getCardType(pstRecord[i].emType));
                    // 使用次数
                    cardDto.setUseTimes(String.valueOf(pstRecord[i].nUserTime));
                    // 是否首卡
                    cardDto.setFirstEnter(pstRecord[i].bFirstEnter);
                    // 是否有效
                    cardDto.setEnable(pstRecord[i].bIsValid);
                    // 有效开始时间
                    cardDto.setStartTime(pstRecord[i].stuValidStartTime.toStringTimeEx());
                    // 有效结束时间
                    cardDto.setEndTime(pstRecord[i].stuValidEndTime.toStringTimeEx());

                    cardDto.setRecordNo(pstRecord[i].nRecNo);
                    // 工种名称
                    cardDto.setTypeOfWork(new String(pstRecord[i].szWorkerTypeName, StandardCharsets.UTF_8).trim());
                    // 部门名称
                    cardDto.setSubpackage(new String(pstRecord[i].szSection, StandardCharsets.UTF_8).trim());
                    //人员照片对应在ftp上的路径
                    cardDto.setSzPhotoPath(new String(pstRecord[i].szPhotoPath, StandardCharsets.UTF_8).trim());

                    //单位名称
                    cardDto.setSzCompanyName(new String(pstRecord[i].szCompanyName, StandardCharsets.UTF_8).trim());
                    //证件地址
                    cardDto.setSzCompanyName(new String(pstRecord[i].szCitizenAddress, StandardCharsets.UTF_8).trim());

                    //施工单位全称
                    cardDto.setSzBuilderName(new String(pstRecord[i].szBuilderName, StandardCharsets.UTF_8).trim());
                    //施工单位类型
                    cardDto.setSzBuilderType(new String(pstRecord[i].szBuilderType, StandardCharsets.UTF_8).trim());
                    //项目名称
                    cardDto.setSzProjectName(new String(pstRecord[i].szProjectName, StandardCharsets.UTF_8).trim());
                    cardDtoList.add(cardDto);

                }

                if (pstRecord.length < nFindCount) {
                    break;
                } else {
                    count++;
                }

            }
        } finally {
            // 关闭查询接口
            GateModule.findCardClose();
        }
        return cardDtoList;
    }
    /**
     * 获取卡列表
     * @return r
     */
    @Override
    public ResultBean getList() {
        List<CardDto> cardDtoList = new LinkedList<>();
        // 卡号：  为空，查询所有的卡信息
        // 获取查询句柄
        if (!GateModule.findCard("")) {
            return ResultBean.success(cardDtoList);
        }
        try {
            int count = 0;
            int index;
            int nFindCount = 10;

            // 查询具体信息
            while (true) {
                NetSDKLib.NET_RECORDSET_ACCESS_CTL_CARD[] pstRecord = GateModule.findNextCard(nFindCount);
                if (pstRecord == null) {
                    break;
                }

                for (int i = 0; i < pstRecord.length; i++) {
                    index = i + count * nFindCount;

                    CardDto cardDto = new CardDto();

                    // 序号
                    cardDto.setSerialNumber(String.valueOf(index + 1));
                    // 卡号
                    cardDto.setCardNo(new String(pstRecord[i].szCardNo).trim());
                    // 卡名
                    cardDto.setCardName(new String(pstRecord[i].szCardName, StandardCharsets.UTF_8).trim());
                    // 用户ID
                    cardDto.setUserId(new String(pstRecord[i].szUserID).trim());
                    // 卡密码
                    cardDto.setCardPasswd(new String(pstRecord[i].szPsw).trim());
                    // 卡状态
                    cardDto.setCardStatus(pstRecord[i].emStatus);
                    cardDto.setCardStatusName(Res.string().getCardStatus(pstRecord[i].emStatus));
                    // 卡类型
                    cardDto.setCardType(pstRecord[i].emType);
                    cardDto.setCardTypeName(Res.string().getCardType(pstRecord[i].emType));
                    // 使用次数
                    cardDto.setUseTimes(String.valueOf(pstRecord[i].nUserTime));
                    // 是否首卡
                    cardDto.setFirstEnter(pstRecord[i].bFirstEnter);
                    // 是否有效
                    cardDto.setEnable(pstRecord[i].bIsValid);
                    // 有效开始时间
                    cardDto.setStartTime(pstRecord[i].stuValidStartTime.toStringTimeEx());
                    // 有效结束时间
                    cardDto.setEndTime(pstRecord[i].stuValidEndTime.toStringTimeEx());

                    cardDto.setRecordNo(pstRecord[i].nRecNo);
                    // 工种名称
                    cardDto.setTypeOfWork(new String(pstRecord[i].szWorkerTypeName, StandardCharsets.UTF_8).trim());
                    // 部门名称
                    cardDto.setSubpackage(new String(pstRecord[i].szSection, StandardCharsets.UTF_8).trim());
                    //人员照片对应在ftp上的路径
                    cardDto.setSzPhotoPath(new String(pstRecord[i].szPhotoPath, StandardCharsets.UTF_8).trim());

                    //单位名称
                    cardDto.setSzCompanyName(new String(pstRecord[i].szCompanyName, StandardCharsets.UTF_8).trim());
                    //证件地址
                    cardDto.setSzCompanyName(new String(pstRecord[i].szCitizenAddress, StandardCharsets.UTF_8).trim());

                    //施工单位全称
                    cardDto.setSzBuilderName(new String(pstRecord[i].szBuilderName, StandardCharsets.UTF_8).trim());
                    //施工单位类型
                    cardDto.setSzBuilderType(new String(pstRecord[i].szBuilderType, StandardCharsets.UTF_8).trim());
                    //项目名称
                    cardDto.setSzProjectName(new String(pstRecord[i].szProjectName, StandardCharsets.UTF_8).trim());
                    NetSDKLib.NET_FACE_FACEDATA[] netFaceFacedata = pstRecord[i].szFaceDataArr;
                    if (netFaceFacedata.length > 0) {
                        NetSDKLib.NET_FACE_FACEDATA netFaceFacedata1 = netFaceFacedata[0];
                        //人脸数据
                        cardDto.setSzProjectName(new String(netFaceFacedata1.szFaceData, StandardCharsets.UTF_8).trim());
                    }

                    cardDtoList.add(cardDto);
                }

                if (pstRecord.length < nFindCount) {
                    break;
                } else {
                    count++;
                }

            }
        } finally {
            // 关闭查询接口
            GateModule.findCardClose();
        }
        return ResultBean.success(cardDtoList);
    }

    /**
     * 校验卡信息
     * @param cardDto 卡信息
     * @param memory 图片信息
     */
    public void checkCardDtoAndMemory(CardDto cardDto, Memory memory) {
        if (memory == null) {
            throw new ServiceException(Res.string().getSelectPicture());
        }
        if (ObjectUtil.isEmpty(cardDto.getCardNo())) {
            throw new ServiceException(Res.string().getInputCardNo());
        }
        if (ObjectUtil.isEmpty(cardDto.getUserId())) {
            throw new ServiceException(Res.string().getInputUserId());
        }
        if (ObjectUtil.isEmpty(cardDto.getCardName())) {
            throw new ServiceException("Please input cardName");
        }
        if (ObjectUtil.isEmpty(cardDto.getCardPasswd())) {
            throw new ServiceException("Please input cardPasswd");
        }

        try {
            if (cardDto.getCardNo().length() > 31) {
                throw new ServiceException(Res.string().getCardNoExceedLength() + "(31)");
            }
            if (cardDto.getUserId().length() > 31) {
                throw new ServiceException(Res.string().getUserIdExceedLength() + "(31)");
            }
            if (cardDto.getCardName() != null && cardDto.getCardName().length() > 63) {
                throw new ServiceException(Res.string().getCardNameExceedLength() + "(63)");
            }
            if (cardDto.getCardPasswd() != null && cardDto.getCardPasswd().length() > 63) {
                throw new ServiceException(Res.string().getCardPasswdExceedLength() + "(63)");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private JSONObject convertBody(CardDto cardDto){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serialNumber",cardDto.getSerialNumber());
        jsonObject.put("cardNo",cardDto.getCardNo());
        jsonObject.put("userId",cardDto.getUserId());
        jsonObject.put("cardName",cardDto.getCardName());
        jsonObject.put("cardPasswd",cardDto.getCardPasswd());
        jsonObject.put("cardStatus",cardDto.getCardStatus());
        jsonObject.put("cardStatusName",cardDto.getCardStatusName());
        jsonObject.put("cardType",cardDto.getCardType());
        jsonObject.put("cardTypeName",cardDto.getCardTypeName());
        jsonObject.put("useTimes",cardDto.getUseTimes());
        jsonObject.put("firstEnter",cardDto.getFirstEnter());
        jsonObject.put("enable",cardDto.getEnable());
        jsonObject.put("startTime",cardDto.getStartTime());
        jsonObject.put("endTime",cardDto.getEndTime());
        jsonObject.put("recordNo",cardDto.getRecordNo());
        jsonObject.put("faceUrl",cardDto.getFaceUrl());
        jsonObject.put("typeOfWork",cardDto.getTypeOfWork());
        jsonObject.put("subpackage",cardDto.getSubpackage());

        return jsonObject;
    }
}
