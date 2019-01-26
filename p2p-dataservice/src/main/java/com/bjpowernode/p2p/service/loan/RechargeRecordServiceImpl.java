package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordServiceImpl
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/14 16:00
 * @Author:guoxin
 */
@Service("rechargeRecordServiceImpl")
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<RechargeRecord> queryRechargeRecordTopByUid(Map<String, Object> paramMap) {
        return rechargeRecordMapper.selectRechargeRecordByPage(paramMap);
    }

    @Override
    public PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap) {
        PaginationVO<RechargeRecord> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(rechargeRecordMapper.selectTotal(paramMap));
        paginationVO.setDataList(rechargeRecordMapper.selectRechargeRecordByPage(paramMap));

        return paginationVO;
    }


    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    public ResultObject recharge(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        resultObject.setErrorMessage("充值成功");

        //更新帐户可用余额
        int updateCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);

        if (updateCount > 0) {
            //更新充值记录的订单状态
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeNo((String) paramMap.get("out_trade_no"));
            rechargeRecord.setRechargeStatus("1");
            int rechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
            if (rechargeCount <= 0) {
                resultObject.setErrorCode(Constants.FAIL);
                resultObject.setErrorMessage("充值失败");
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
            resultObject.setErrorMessage("充值失败");
        }


        return resultObject;
    }
}
