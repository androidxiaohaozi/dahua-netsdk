package com.smartsite.netsdk.listener;

import com.smartsite.netsdk.callback.AnalyzerDataCB;
import com.smartsite.netsdk.common.Res;
import com.smartsite.netsdk.demo.module.GateModule;
import com.smartsite.netsdk.demo.module.LoginModule;
import com.smartsite.netsdk.lib.NetSDKLib;
import com.smartsite.netsdk.utils.DeviceUtils;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/27 16:15
 */
//@Component
@Component
@Slf4j
public class ApplicationEventListener  implements ApplicationRunner, DisposableBean {
    public static NetSDKLib.LLong m_hAttachHandle = new NetSDKLib.LLong(0);

    public static NetSDKLib.LLong m_17hAttachHandle = new NetSDKLib.LLong(0);
    public static NetSDKLib.LLong m_26hAttachHandle = new NetSDKLib.LLong(0);
    public static NetSDKLib.LLong m_28hAttachHandle = new NetSDKLib.LLong(0);
    public static NetSDKLib.LLong m_29hAttachHandle = new NetSDKLib.LLong(0);
    private AnalyzerDataCB analyzerCallback = new AnalyzerDataCB();
    private boolean isAttach = false;

    private static NetSDKLib.LLong videoStatHandle = new NetSDKLib.LLong(0);

    @Async("taskExecutor")
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("调用了ApplicationEventListener的run方法");
        //1 注册
        LoginModule.init(DIS_CONNECT, HAVE_RE_CONNECT);
        //2 登录
        DeviceUtils.login("1");

        //2 登录
        DeviceUtils.login("3");
        //2 登录
        DeviceUtils.login("4");
        //2 登录
        DeviceUtils.login("5");
        //2 登录
        DeviceUtils.login("6");
        //3 注册
        m_hAttachHandle = GateModule.realLoadPic(0, analyzerCallback);
        if(m_hAttachHandle.longValue() != 0) {
            isAttach = true;
            log.info("注册成功");
        } else {
            log.info("注册失败");
        }

        //3 注册
        m_17hAttachHandle = GateModule.real17LoadPic(0, analyzerCallback);
        if(m_17hAttachHandle.longValue() != 0) {
            isAttach = true;
            log.info("注册成功");
        } else {
            log.info("注册失败");
        }
        //3 注册
        m_26hAttachHandle = GateModule.real26LoadPic(0, analyzerCallback);
        if(m_26hAttachHandle.longValue() != 0) {
            isAttach = true;
            log.info("注册成功");
        } else {
            log.info("注册失败");
        }
        //3 注册
        m_28hAttachHandle = GateModule.real28LoadPic(0, analyzerCallback);
        if(m_28hAttachHandle.longValue() != 0) {
            isAttach = true;
            log.info("注册成功");
        } else {
            log.info("注册失败");
        }
        //3 注册
        m_29hAttachHandle = GateModule.real29LoadPic(0, analyzerCallback);
        if(m_29hAttachHandle.longValue() != 0) {
            isAttach = true;
            log.info("注册成功");
        } else {
            log.info("注册失败");
        }
    }

    @Async("taskExecutor")
    @Override
    public void destroy() throws Exception {
        log.info("调用了ApplicationEventListener的destory方法");
        //1 取消注册
        GateModule.stopRealLoadPic(m_hAttachHandle);
        //1 取消注册
        GateModule.stopRealLoadPic(m_17hAttachHandle);
        //1 取消注册
        GateModule.stopRealLoadPic(m_26hAttachHandle);
        //1 取消注册
        GateModule.stopRealLoadPic(m_28hAttachHandle);
        //1 取消注册
        GateModule.stopRealLoadPic(m_29hAttachHandle);
        //2 清除环境
        LoginModule.cleanup();
        //3 退出
        LoginModule.logout();
    }

    /**
     * // 设备断线回调: 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
     */
    private class DisConnect implements NetSDKLib.fDisConnect {
        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            log.info("Device[{}}] Port[{}}] DisConnect!\n", pchDVRIP, nDVRPort);
            // 断线提示
            log.info(Res.string().getFaceRecognition() + " : " + Res.string().getDisConnectReconnecting());
        }
    }

    /**
     * // 网络连接恢复，设备重连成功回调
     * // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
     */
    private class HaveReConnect implements NetSDKLib.fHaveReConnect {
        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            log.info("ReConnect Device[{}] Port[{}]\n", pchDVRIP, nDVRPort);
            // 重连提示
            log.info(Res.string().getFaceRecognition() + " : " + Res.string().getOnline());
        }
    }

    /**
     * 设备断线通知回调
     */
    private final DisConnect DIS_CONNECT = new DisConnect(){};

    /**
     * 网络连接恢复
     */
    private final HaveReConnect HAVE_RE_CONNECT = new HaveReConnect();

}
