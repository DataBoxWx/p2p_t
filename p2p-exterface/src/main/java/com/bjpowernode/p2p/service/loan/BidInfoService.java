package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/12 9:27
 * @Author:guoxin
 */
public interface BidInfoService {
    /**
     * 获取平台累计投资金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取产品的所有投资记录
     * @param id
     * @return
     */
    List<BidInfo> queryBidInfoListByProductId(Integer id);

    /**
     * 根据用户标识获取最近的投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询投资记录
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 用户投资
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);
}
