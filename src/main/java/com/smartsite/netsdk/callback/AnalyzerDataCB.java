package com.smartsite.netsdk.callback;

import com.alibaba.fastjson2.JSONObject;
import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.lib.NetSDKLib;
import com.smartsite.netsdk.lib.ToolKits;
import com.smartsite.netsdk.listener.ApplicationEventListener;
import com.smartsite.netsdk.service.MinioService;
import com.smartsite.netsdk.utils.HttpClientUtil;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    /**
     * 门禁同步
     */
    @Value("${transVideoAlarmSaveDataUrl}")
    private String transVideoAlarmSaveDataUrl;

    @Resource
    private MinioService minioService;

    @Override
    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                      Pointer dwUser, int nSequence, Pointer reserved) {
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

            String szSection = new String(msg.szSection, StandardCharsets.UTF_8);
            System.out.printf("部门：%s\n",szSection);
            String szClassNumberEx = new String(msg.szClassNumberEx, StandardCharsets.UTF_8);
            System.out.printf("班组：%s\n",szClassNumberEx);

            NetSDKLib.DEV_ACCESS_CTL_CUSTOM_WORKER_INFO stuCustomWorkerInfo = msg.stuCustomWorkerInfo;
            String szWorkerTypeID = new String(stuCustomWorkerInfo.szWorkerTypeID, StandardCharsets.UTF_8);
            System.out.printf("工种id：%s\n",szWorkerTypeID);
            String szWorkerTypeName = new String(stuCustomWorkerInfo.szWorkerTypeName, StandardCharsets.UTF_8);
            System.out.printf("工种名称：%s\n",szWorkerTypeName);

            for (NetSDKLib.DEV_ACCESS_CTL_IMAGE_INFO devAccessCtlImageInfo : msg.stuImageInfo) {
                int emType = devAccessCtlImageInfo.emType;
                if (emType == -1) {
                    System.out.print("未知");
                } else if (emType == 0) {
                    System.out.print("本地人脸图库");
                } else if (emType == 1) {
                    System.out.print("拍摄场景抠图");
                } else {
                    System.out.print("人脸抠图");
                }
            }

            int emEventType = msg.emEventType;
            JSONObject jsonObject = new JSONObject();
            System.out.println("emEventType = " + emEventType);
            if (1 == emEventType) {
                jsonObject.put("describe","进门");
                System.out.print("进门");
            } else if (2 == emEventType) {
                jsonObject.put("describe","出门");
                System.out.print("出门");
            }

            jsonObject.put("openMode",emOpenMethod);
            jsonObject.put("userId",szUserId);
            jsonObject.put("userName",szCardName);

//            HttpClientUtil.httpPost(accessControlRecordsUrl,jsonObject);

            //工装(安全帽/工作服等)检测事件
        } else if (dwAlarmType == NetSDKLib.EVENT_IVS_WORKCLOTHES_DETECT) {
            logger.info("监听到工装(安全帽/工作服等)检测事件了");
            JSONObject alarmJSONObject = new JSONObject();

            if (lAnalyzerHandle.equals(ApplicationEventListener.m_17hAttachHandle) ) {
                alarmJSONObject.put("position","192.168.1.17");
                System.out.println("position：192.168.1.17");
            } else if (lAnalyzerHandle.equals(ApplicationEventListener.m_26hAttachHandle)) {
                alarmJSONObject.put("position","192.168.1.26");
                System.out.println("position：192.168.1.26");
            } else if (lAnalyzerHandle.equals(ApplicationEventListener.m_28hAttachHandle)) {
                alarmJSONObject.put("position","192.168.1.28");
                System.out.println("position：192.168.1.28");
            } else if (lAnalyzerHandle.equals(ApplicationEventListener.m_29hAttachHandle)) {
                alarmJSONObject.put("position","192.168.1.29");
                System.out.println("position：192.168.1.29");
            } else {
                alarmJSONObject.put("position","未知");
                System.out.println("position：未知");
            }

            String path = "/app/pic";
            NetSDKLib.DEV_EVENT_WORKCLOTHES_DETECT_INFO msg = new NetSDKLib.DEV_EVENT_WORKCLOTHES_DETECT_INFO();
            ToolKits.GetPointerData(pAlarmInfo, msg);
            int nAlarmType = msg.nAlarmType;
            if (nAlarmType == 0) {
                System.out.println("检测类别：未知");
                alarmJSONObject.put("type","未知");
            } else if (nAlarmType == 1) {
                System.out.println("检测类别：防护服不规范");
                alarmJSONObject.put("type","防护服不规范");
            } else if (nAlarmType == 2) {
                System.out.println("检测类别：工作服不规范");
                alarmJSONObject.put("type","工作服不规范");
            } else if (nAlarmType == 3) {
                System.out.println("检测类别：安全帽不规范");
                alarmJSONObject.put("type","安全帽不规范");
            } else if (nAlarmType == 4) {
                System.out.println("检测类别：安全帽和工作服不规范");
                alarmJSONObject.put("type","安全帽和工作服不规范");
            }

            NetSDKLib.NET_TIME_EX ex =  msg.UTC;
            String s = ex.toString();
            System.out.println("监测时间=" + s);
            alarmJSONObject.put("time",s);

            NetSDKLib.SCENE_IMAGE_INFO stuSceneImage = msg.stuSceneImage;
            if (stuSceneImage != null) {
                //偏移量
                int nOffSet = stuSceneImage.nOffSet;
                System.out.printf("全景大图信息nOffSet：%d\n", nOffSet);
                //图片大小
                int nLength = stuSceneImage.nLength;
                System.out.printf("全景大图信息图片大小：%d\n", nLength);
                //在上传图片数据中的图片序号
                int nIndexInData = stuSceneImage.nIndexInData;
                System.out.printf("全景大图信息nIndexInData：%d\n", nIndexInData);

                //将图片保存到本地
                System.out.println("new byte[nLength]");
                byte[] byteArray = pBuffer.getByteArray(nOffSet, nLength);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                System.out.println("pBuffer.read");
                String strFileName = path + "//" + System.currentTimeMillis() + "quanjing_snap.jpg";
                try{
                    BufferedImage snapBufferedImage = ImageIO.read(byteArrayInputStream);
                    if (snapBufferedImage == null) {
                        System.out.println("BufferedImage read = ImageIO.read(byteArrayInputStream); == null");
                    } else {
                        ImageIO.write(snapBufferedImage,"jpg",new File(strFileName));
                    }

                    File uploadPic = new File(strFileName);
                    MultipartFile cMultiFile = new MockMultipartFile("file", uploadPic.getName(), null, new FileInputStream(uploadPic));
                    ResultBean resultBean = minioService.uploadFile(cMultiFile);
                    Map<String,Object> minioMap = (Map<String, Object>)resultBean.get("data");
                    Object fileName = minioMap.get("fileName");
                    System.out.println("picName = " + fileName);
                    alarmJSONObject.put("pic",fileName);
                    boolean b = uploadPic.delete();
                }catch (Exception e) {
                    e.printStackTrace();;
                }
            } else {
                System.out.print("全景大图信息为空");
            }
            HttpClientUtil.httpPost(transVideoAlarmSaveDataUrl,alarmJSONObject);
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
