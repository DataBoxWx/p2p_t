package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BidInfoServiceImpl
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/12 9:27
 * @Author:guoxin
 */
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Double queryAllBidMoney() {
        //设置redis中key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //获取指定key的操作对象
        BoundValueOperations<Object, Object> boundValueOps = redisTemplate.boundValueOps(Constants.ALL_BID_MONEY);

        //获取操作对象的值
        Double allBidMoney = (Double) boundValueOps.get();

        //判断是否为空
        if (null == allBidMoney) {
            //去数据库查询
            allBidMoney = bidInfoMapper.selectAllBidMoney();

            //将该值存放到Redis缓存中
            boundValueOps.set(allBidMoney,15, TimeUnit.MINUTES);
        }


        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByProductId(Integer id) {
        return bidInfoMapper.selectBidInfoListByProductId(id);
    }

    @Override
    public List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectBidInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));
        paginationVO.setDataList(bidInfoMapper.selectBidInfoByPage(paramMap));

        return paginationVO;
    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        resultObject.setErrorMessage("投资成功");


        //超卖现象:实际销售的数量超过计划数量
        //解决方法:通过数据库的乐观锁机制

        //获取当前数据的版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));
        paramMap.put("version",loanInfo.getVersion());

        //更新产品剩余可投金额
        int updateLeftProductMoneyCount = loanInfoMapper.updateLoanInfoLeftProductMoneyByLoanId(paramMap);

        if (updateLeftProductMoneyCount > 0) {
            //更新帐户可用余额
            int updateFinanceCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);

            if (updateFinanceCount > 0) {
                //新增投资记录
                BidInfo bidInfo = new BidInfo();
                bidInfo.setUid((Integer) paramMap.get("uid"));
                bidInfo.setLoanId((Integer) paramMap.get("loanId"));
                bidInfo.setBidMoney((Double) paramMap.get("bidMoney"));
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);

                int insertCount = bidInfoMapper.insertSelective(bidInfo);

                if (insertCount > 0) {
                    //获取产品的详情
                    LoanInfo loan = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));

                    //判断产品是否满标
                    if (0 == loan.getLeftProductMoney()) {
                        //产品已满标:更新产品的满标时间及产品状态
                        LoanInfo loanDetail = new LoanInfo();
                        loanDetail.setId(loan.getId());
                        loanDetail.setProductFullTime(new Date());
                        loanDetail.setProductStatus(1);//0未满标,1已满标,2满标且生成收益计划

                        loanInfoMapper.updateByPrimaryKeySelective(loanDetail);
                    }




                } else {
                    resultObject.setErrorCode(Constants.FAIL);
                    resultObject.setErrorMessage("投资失败");
                }

            } else {
                resultObject.setErrorCode(Constants.FAIL);
                resultObject.setErrorMessage("投资失败");
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
            resultObject.setErrorMessage("投资失败");
        }

        return resultObject;
    }
}
