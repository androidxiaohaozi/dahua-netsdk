package com.smartsite.netsdk.lib.structure;


import com.smartsite.netsdk.lib.NetSDKLib;

/** 
CLIENT_TransferSubLinkInfo 接口输出参数
*/
public class NET_OUT_TRANSFER_SUBLINK_INFO extends NetSDKLib.SdkStructure {
/** 
/< 结构体大小
*/
public			int					dwSize;

public NET_OUT_TRANSFER_SUBLINK_INFO(){
    this.dwSize=this.size();
}
}