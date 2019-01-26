package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LoanInfoServiceImpl
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/10 15:56
 * @Author:guoxin
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryHistoryAverageRate() {

        //设置key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //首先去redis缓存中查询,如果有:直接使用,没有:去数据库查询,并放到redis缓存,设置失效时间
        //好处:减少对数据库的访问,提升系统的性能


        //从redis缓存中获取该值
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //判断是否有值
        if (null == historyAverageRate) {
            //没有:去数据库查询
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
            //将该值存放到redis缓存对象中
            redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES);

        }
        //有:直接使用

        return historyAverageRate;
    }


    @Override
    public List<LoanInfo> queryLoanInfoByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoByPage(paramMap);
    }


    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<LoanInfo>();

        paginationVO.setTotal(loanInfoMapper.selectTotal(paramMap));
        paginationVO.setDataList(loanInfoMapper.selectLoanInfoByPage(paramMap));


        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
}















