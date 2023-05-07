package com.smartsite.netsdk.callback;

import com.alibaba.fastjson2.JSONObject;
import com.smartsite.netsdk.lib.NetSDKLib;
import com.smartsite.netsdk.lib.ToolKits;
import com.smartsite.netsdk.utils.HttpClientUtil;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/27 16:44
 */
public class AnalyzerDataCB implements NetSDKLib.fAnalyzerDataCallBack {
    private BufferedImage gateBufferedImage = null;
    private static Logger logger = LoggerFactory.getLogger(AnalyzerDataCB.class);
    /**
     * 门禁同步
     */
    @Value("${accessControlRecordsUrl}")
    private String accessControlRecordsUrl;
    @Override
    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                      Pointer dwUser, int nSequence, Pointer reserved) {
        System.out.println("AnalyzerDataCB.invoke method zhixingl");
        if (lAnalyzerHandle.longValue() == 0 || pAlarmInfo == null) {
            return -1;
        }

/*        File path = new File("./GateSnapPicture/");
        if (!path.exists()) {
            path.mkdir();
        }*/
        ///< 门禁事件
        if(dwAlarmType == NetSDKLib.EVENT_IVS_ACCESS_CTL) {
            logger.info("监听到门禁时间了");
            NetSDKLib.DEV_EVENT_ACCESS_CTL_INFO msg = new NetSDKLib.DEV_EVENT_ACCESS_CTL_INFO();
            ToolKits.GetPointerData(pAlarmInfo, msg);
            // 获取门禁信息
            //打印msg中的卡信息和用戶信息
            int szCardNo = ByteBuffer.wrap(msg.szCardNo).getInt();
            System.out.println("卡号：" + szCardNo);
            int szUserId = ByteBuffer.wrap(msg.szUserID).getInt();
            System.out.printf("用户ID：%d\n", szUserId);
            Long szCitizenIDNo = ByteBuffer.wrap(msg.szCitizenIDNo).getLong();
            System.out.printf("证件号：%d\n", szCitizenIDNo);
            String szCardName = new String(msg.szCardName, StandardCharsets.UTF_8);
            System.out.printf("卡命名：%s\n", szCardName);
            String emOpenMethod = getEmOpenMethod(msg.emOpenMethod);
            System.out.printf("开门方式：%s\n", emOpenMethod);

            int emEventType = msg.emEventType;
            JSONObject jsonObject = new JSONObject();
            if (1 == emEventType) {
                jsonObject.put("describe","进门");
            } else if (2 == emEventType) {
                jsonObject.put("describe","出门");
            }

            jsonObject.put("openMode",emOpenMethod);
            jsonObject.put("userId",szUserId);
            jsonObject.put("userName",szCardName);

            HttpClientUtil.httpPost(accessControlRecordsUrl,jsonObject);

        }

        return 0;
    }

    /**
     * 获取开门方式
     * @param emOpenMethod e
     * @return r
     */
    private String getEmOpenMethod(int emOpenMethod){

        String emOpenMethodStr = "";

        switch (emOpenMethod){
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_UNKNOWN:
                emOpenMethodStr = "未知";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_PWD_ONLY:
                emOpenMethodStr = "密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD:
                emOpenMethodStr = "刷卡开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_FIRST:
                emOpenMethodStr = "先刷卡后密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_PWD_FIRST:
                emOpenMethodStr = "先密码后刷卡开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_REMOTE:
                emOpenMethodStr = "远程开锁,如通过室内机或者平台对门口机开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_BUTTON:
                emOpenMethodStr = "开锁按钮进行开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT:
                emOpenMethodStr = "信息开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_PWD_CARD_FINGERPRINT:
                emOpenMethodStr = "密码+刷卡+信息组合开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_PWD_FINGERPRINT:
                emOpenMethodStr = "密码+信息组合开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_FINGERPRINT:
                emOpenMethodStr = "刷卡+信息组合开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_PERSONS:
                emOpenMethodStr = "多人开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_KEY:
                emOpenMethodStr = "钥匙开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_COERCE_PWD:
                emOpenMethodStr = "胁迫密码开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_QRCODE:
                emOpenMethodStr = "二维码开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACE_RECOGNITION:
                emOpenMethodStr = "目标识别开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACEIDCARD:
                emOpenMethodStr = "人证对比";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACEIDCARD_AND_IDCARD:
                emOpenMethodStr = "证件+ 人证比对";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_BLUETOOTH:
                emOpenMethodStr = "蓝牙开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CUSTOM_PASSWORD:
                emOpenMethodStr = "个性化密码开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_USERID_AND_PWD:
                emOpenMethodStr = "UserID+密码";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACE_AND_PWD:
                emOpenMethodStr = "人脸+密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_AND_PWD:
                emOpenMethodStr = "信息+密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_AND_FACE:
                emOpenMethodStr = "信息+人脸开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_AND_FACE:
                emOpenMethodStr = "刷卡+人脸开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACE_OR_PWD:
                emOpenMethodStr = "人脸或密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_OR_PWD:
                emOpenMethodStr = "信息或密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_OR_FACE:
                emOpenMethodStr = "信息或人脸开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_OR_FACE:
                emOpenMethodStr = "刷卡或人脸开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_OR_FINGERPRINT:
                emOpenMethodStr = "刷卡或信息开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_AND_FACE_AND_PWD:
                emOpenMethodStr = "信息+人脸+密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_AND_FACE_AND_PWD:
                emOpenMethodStr = "刷卡+人脸+密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_AND_FINGERPRINT_AND_PWD:
                emOpenMethodStr = "刷卡+信息+密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_AND_PWD_AND_FACE:
                emOpenMethodStr = "卡+信息+人脸组合开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FINGERPRINT_OR_FACE_OR_PWD:
                emOpenMethodStr = "信息或人脸或密码";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_OR_FACE_OR_PWD:
                emOpenMethodStr = "卡或人脸或密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_OR_FINGERPRINT_OR_FACE:
                emOpenMethodStr = "卡或信息或人脸开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_AND_FINGERPRINT_AND_FACE_AND_PWD:
                emOpenMethodStr = "卡+信息+人脸+密码组合开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CARD_OR_FINGERPRINT_OR_FACE_OR_PWD:
                emOpenMethodStr = "卡或信息或人脸或密码开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACEIPCARDANDIDCARD_OR_CARD_OR_FACE:
                emOpenMethodStr = "(证件+人证比对)或 刷卡 或 人脸";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_FACEIDCARD_OR_CARD_OR_FACE:
                emOpenMethodStr = "(人证比对 或 刷卡(二维码) 或 人脸";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_DTMF:
                emOpenMethodStr = "DTMF开锁";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_REMOTE_QRCODE:
                emOpenMethodStr = "远程二维码开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_REMOTE_FACE:
                emOpenMethodStr = "远程人脸开门";
                break;
            case NetSDKLib.NET_ACCESS_DOOROPEN_METHOD.NET_ACCESS_DOOROPEN_METHOD_CITIZEN_FINGERPRINT:
                emOpenMethodStr = "人证比对开门(信息)";
                break;
            default:
                emOpenMethodStr = "未知信息";
        }

        return emOpenMethodStr;
    }
}
