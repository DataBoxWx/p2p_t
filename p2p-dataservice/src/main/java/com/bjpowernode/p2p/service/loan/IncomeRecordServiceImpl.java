package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.util.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClassName:IncomeRecordServiceImpl
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/14 16:32
 * @Author:guoxin
 */
@Service("incomeRecordServiceImpl")
public class IncomeRecordServiceImpl implements IncomeRecordService {

    private Logger logger = LogManager.getLogger(IncomeRecordServiceImpl.class);

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<IncomeRecord> queryIncomeRecordTopByUid(Map<String, Object> paramMap) {
        return incomeRecordMapper.selectIncomeRecordByPage(paramMap);
    }

    @Override
    public PaginationVO<IncomeRecord> queryIncomeRecordByPage(Map<String, Object> paramMap) {
        PaginationVO<IncomeRecord> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(incomeRecordMapper.selectTotal(paramMap));
        paginationVO.setDataList(incomeRecordMapper.selectIncomeRecordByPage(paramMap));

        return paginationVO;
    }

    @Override
    public void generateIncomePlan() {

        //查询产品状态为已满标的产品 -> 返回List<产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByProductStatus(1);

        //循环遍历List<产品> ->获取到每一个产品
        for (LoanInfo loanInfo:loanInfoList) {

            Date productFullTime = loanInfo.getProductFullTime();
            Integer cycle = loanInfo.getCycle();
            Double rate = loanInfo.getRate();


            //根据产品标识获取所有的投资记录   -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByProductId(loanInfo.getId());

            //循环遍历List<投资记录>    -> 获取到每一条的投资记录
            for (BidInfo bidInfo:bidInfoList) {

                //将每一条的投资记录生成对应的收益记录
                IncomeRecord incomeRecord = new IncomeRecord();

                incomeRecord.setUid(bidInfo.getUid());//用户标识
                incomeRecord.setLoanId(loanInfo.getId());//投资产品标识
                incomeRecord.setBidId(bidInfo.getId());//投资记录标识
                incomeRecord.setBidMoney(bidInfo.getBidMoney());//投资金额
                incomeRecord.setIncomeStatus(0);//0未返还,1已返还

                //收益时间(Date) = 满标时间(Date) + 产品周期(Integer)
                Date incomeDate = null;

                //收益金额 = 投资金额 * 天利率 * 投资天数
                Double incomeMoney = null;

                if (0 == loanInfo.getProductType()) {
                    //新手宝
                    incomeDate = DateUtils.getDateByAddDays(productFullTime,cycle);
                    incomeMoney = bidInfo.getBidMoney() * (rate / 100 / DateUtils.getDaysByYear(Calendar.getInstance().get(Calendar.YEAR))) * cycle;
                } else {
                    //优先和散标
                    incomeDate = DateUtils.getDateByAddMonths(productFullTime,cycle);
                    incomeMoney = bidInfo.getBidMoney() * (rate / 100 / DateUtils.getDaysByYear(Calendar.getInstance().get(Calendar.YEAR))) * DateUtils.getInstanceBetweenDate(productFullTime,incomeDate);
                }

                incomeMoney = Math.round(incomeMoney * Math.pow(10,2)) / Math.pow(10,2);


                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);


            }

            //更新当前产品的状态为2满标且生成收益计划
            LoanInfo loanDetail = new LoanInfo();
            loanDetail.setId(loanInfo.getId());
            loanDetail.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanDetail);

        }
    }


    @Override
    public void generateIncomeBack() {

        //查询收益状态为0且收益时间与当前时间相同的收益记录 -> 返回List<收益记录>
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeStatusAndIncomeDate(0);

        //循环遍历List<收益记录>
        for (IncomeRecord incomeRecord:incomeRecordList) {
            //将每一条的收益记录里的收益金额和投资金额返还给对应的用户
            //更新帐户可用余额
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());

            int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            if (updateCount > 0) {

                //更新当前收益记录的状态为1已返还
                IncomeRecord incomeDetail = new IncomeRecord();
                incomeDetail.setId(incomeRecord.getId());
                incomeDetail.setIncomeStatus(1);
                incomeRecordMapper.updateByPrimaryKeySelective(incomeDetail);

            } else {
                logger.info("用户标识为" + incomeRecord.getUid() + ",投资标识为" + incomeRecord.getBidId() + ",收益返还失败");
            }
        }

    }
}













