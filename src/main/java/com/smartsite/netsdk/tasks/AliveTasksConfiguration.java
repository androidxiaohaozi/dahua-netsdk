package com.smartsite.netsdk.tasks;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smartsite.netsdk.bean.CardDto;
import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.service.CardManageService;
import com.smartsite.netsdk.utils.HttpClientUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/29 15:52
 */
@Configuration
@EnableScheduling
public class AliveTasksConfiguration {

    /**
     * 新增
     */
    @Value("${cardManageAddUrl}")
    private String cardManageAddUrl;

    @Resource
    private CardManageService cardManageService;

    @Scheduled(cron = "0/30 * * * * ?")
    private void configureTasks() {
        System.err.println("我还活着呢: " + LocalDateTime.now());
       /* List<CardDto> listForBody = cardManageService.getListForBody();
        JSONArray jsonArray = new JSONArray();
        JSONObject itemJSONObject;
        if (listForBody != null && listForBody.size() > 0) {
            for (CardDto cardDto : listForBody) {
                itemJSONObject = new JSONObject();
                itemJSONObject.put("serialNumber",cardDto.getSerialNumber());
                itemJSONObject.put("cardNo",cardDto.getCardNo());
                itemJSONObject.put("userId",cardDto.getUserId());
                itemJSONObject.put("cardName",cardDto.getCardName());
                itemJSONObject.put("cardPasswd",cardDto.getCardPasswd());
                itemJSONObject.put("cardStatus",cardDto.getCardStatus());
                itemJSONObject.put("cardStatusName",cardDto.getCardStatusName());
                itemJSONObject.put("cardType",cardDto.getCardType());
                itemJSONObject.put("cardTypeName",cardDto.getCardTypeName());
                itemJSONObject.put("useTimes",cardDto.getUseTimes());
                itemJSONObject.put("firstEnter",cardDto.getFirstEnter());
                itemJSONObject.put("enable",cardDto.getEnable());
                itemJSONObject.put("startTime",cardDto.getStartTime());
                itemJSONObject.put("endTime",cardDto.getEndTime());
                itemJSONObject.put("recordNo",cardDto.getRecordNo());
                itemJSONObject.put("faceUrl",cardDto.getFaceUrl());
                itemJSONObject.put("typeOfWork",cardDto.getTypeOfWork());
                itemJSONObject.put("subpackage",cardDto.getSubpackage());
                jsonArray.add(itemJSONObject);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonArray",jsonArray);
        HttpClientUtil.httpPost(cardManageAddUrl, jsonObject);*/
    }
}
