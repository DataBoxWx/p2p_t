package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map; /**
 * ClassName:RechargeRecordService
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/14 15:59
 * @Author:guoxin
 */
public interface RechargeRecordService {
    /**
     * 根据用户标识获取最近的充值记录
     * @param paramMap
     * @return
     */
    List<RechargeRecord> queryRechargeRecordTopByUid(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询充值记录
     * @param paramMap
     * @return
     */
    PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap);

    /**
     * 新增充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 根据充值订单号修改充值记录信息
     * @param rechargeRecord
     * @return
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 用户充值
     * @param paramMap
     * @return
     */
    ResultObject recharge(Map<String, Object> paramMap);
}
