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
@Slf4j
public class ApplicationEventListener  implements ApplicationRunner, DisposableBean {
    public static NetSDKLib.LLong m_hAttachHandle = new NetSDKLib.LLong(0);
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

        //3 注册
        m_hAttachHandle = GateModule.realLoadPic(0, analyzerCallback);
        if(m_hAttachHandle.longValue() != 0) {
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
