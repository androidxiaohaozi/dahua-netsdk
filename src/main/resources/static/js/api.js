/**
 * api 路径封装
 * Hajax 请求封装
 */
 const HOST = "http://192.168.8.121:8080"; // 测试
 // const HOST = "http://60.6.250.4:12734"; // 正式

//const app = getApp()
// 接口地址
var PATH = {
    // 登录接口
    saveFile: "/minio/upload",//上传接口
    addImgFile: "/wechat/upload/add",//新增
    updateImgFile: '/wechat/upload/update',//修改
    selectFileList: "/wechat/upload/queryByConditionForUser",//列表

    // 日常检查接口
    select: "/cardManage/getList",//查询
    insert: "/cardManage/add",//新增
    update: '/wechat/inspectDaily/update',//修改
    delete:'/wechat/inspectDaily/delete',//删除
    //upload:'/platform/upload'//上传
};

/**
 * HTTP请求封装
 * url 接口地址
 * params 参数 json类型
 * type 接口类型
 * success 成功的回调函数
 * fail 失败的回调函数
 */
var GetAjax = function (url, type, synchro, success, fail) {
    $.ajax({
        url: HOST + url,
        type: type ? type : 'get',
        async: synchro ? true : synchro,
        dataType: 'json',
        processData: false,
        contentType: false,
        success: function (res) {
            success(res);
        },
        fail: function (res) {
            console.log(res);
            //fail(res);
        }
    });
}
var Hajax1 = function (url, type, synchro, params, success, fail) {
    $.ajax({
        url: HOST + url,
        data: JSON.stringify(params) || {},
        type: type ? type : 'get',
        async: synchro ? true : synchro,
        dataType: 'json',
        processData: false,
        contentType: 'application/json',
        success: function (res) {
            success(res);
        },
        fail: function (res) {
            console.log(res);
        }
    });
}
var Hajax = function (url, type, synchro,params, success, fail) {
    $.ajax({
        url: HOST + url,
        data: params || {},
        type: type ? type : 'get',
        async: synchro ? true : synchro,
        dataType: 'json',
        processData: false,
        contentType: false,
        success: function (res) {
            success(res);
        },
        fail: function (res) {
            console.log(res);
            //fail(res);
        }
    });
}


var ajaxTextReq = function (url, type, synchro, params, success, fail) {
    $.ajax({
        url: HOST + url,
        data: JSON.stringify(params) || {},
        type: type ? type : 'get',
        async: synchro ? true : synchro,
        dataType: 'json',
        processData: false,
        contentType: 'application/json',
        success: function (res) {
            success(res);
        },
        fail: function (res) {
        }
    });
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;

}

