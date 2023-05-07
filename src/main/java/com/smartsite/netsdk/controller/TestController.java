package com.smartsite.netsdk.controller;

import com.smartsite.netsdk.callback.AnalyzerDataCB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/28 15:44
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(AnalyzerDataCB.class);
    @GetMapping("/getTestMsg")
    public String getTestMsg(){
        logger.info("logger info ");
        System.out.println("system out println");
        return "testMsg";
    }
}
