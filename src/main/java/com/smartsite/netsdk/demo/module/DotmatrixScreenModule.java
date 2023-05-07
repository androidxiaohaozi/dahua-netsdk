package com.smartsite.netsdk.demo.module;


import com.smartsite.netsdk.lib.NetSDKLib;

public class DotmatrixScreenModule {
	
	
	public static boolean setDotmatrixScreen(int emType, NetSDKLib.NET_CTRL_SET_PARK_INFO msg) {
		
		boolean ret = LoginModule.netsdk.CLIENT_ControlDevice(LoginModule.m_hLoginHandle, emType, msg.getPointer(), 3000);
		
		return ret;	
	}
}
