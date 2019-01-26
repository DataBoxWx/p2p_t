package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map; /**
 * ClassName:IncomeRecordService
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/14 16:32
 * @Author:guoxin
 */
public interface IncomeRecordService {
    /**
     * 根据用户标识获取最近的收益记录
     * @param paramMap
     * @return
     */
    List<IncomeRecord> queryIncomeRecordTopByUid(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询收益记录
     * @param paramMap
     * @return
     */
    PaginationVO<IncomeRecord> queryIncomeRecordByPage(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void generateIncomePlan();

    /**
     * 收益返还
     */
    void generateIncomeBack();
}
