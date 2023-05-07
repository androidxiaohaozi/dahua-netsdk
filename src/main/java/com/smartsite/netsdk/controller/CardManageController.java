package com.smartsite.netsdk.controller;

import com.smartsite.netsdk.bean.CardDto;
import com.smartsite.netsdk.common.ResultBean;
import com.smartsite.netsdk.service.CardManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 8:55
 */
@RestController
@RequestMapping("/cardManage")
public class CardManageController {

    @Resource
    private CardManageService cardManageService;

    /**
     * 新增
     * @param cardDto c
     * @return r
     * @throws IOException e
     */
    @PostMapping("/add")
    public ResultBean add(@RequestBody CardDto cardDto) throws IOException {
        System.out.println(cardDto.getCardName());
        cardManageService.add(cardDto);
        return ResultBean.success();
    }

    /**
     * 修改
     * @param cardDto c
     * @return r
     * @throws IOException e
     */
    @PostMapping("/update")
    public ResultBean update(@RequestBody CardDto cardDto) throws IOException {
        System.out.println(cardDto.getCardName());
        cardManageService.update(cardDto);
        return ResultBean.success();
    }

    /**
     * 获取列表
     * @return r
     */
    @GetMapping("/getList")
    public ResultBean getList(){
        return cardManageService.getList();
    }

    /**
     * 删除
     * @param recordNo n
     * @param userId r
     * @return r
     */
    @GetMapping("/delete")
    public ResultBean delete( String recordNo,String userId ) {
        return cardManageService.delete(recordNo,userId);
    }
}
