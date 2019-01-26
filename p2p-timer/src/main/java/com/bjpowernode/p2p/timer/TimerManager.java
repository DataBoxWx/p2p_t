package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName:TaskTest
 * package:com.bjpowernode.p2p.timer
 * Descrption:
 *
 * @Date:2018/7/16 12:24
 * @Author:guoxin
 */
@Component
public class TimerManager {

    private Logger logger = LogManager.getLogger(TimerManager.class);

    @Autowired
    private IncomeRecordService incomeRecordService;

//    @Scheduled(cron = "0/3 * * * * *")
    public void generateIncomePlan() {
        logger.info("------------生成收益计划开始------------");
        incomeRecordService.generateIncomePlan();
        logger.info("------------生成收益计划结束------------");
    }

    @Scheduled(cron = "0/5 * * * * *")
    public void generateIncomeBack() {
        logger.info("------------收益返回开始------------");
        incomeRecordService.generateIncomeBack();
        logger.info("------------收益返回结束------------");
    }
}
