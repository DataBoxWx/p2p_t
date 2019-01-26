package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int insert(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int insertSelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    BidInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int updateByPrimaryKeySelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品标识获取产品的所有投资记录
     * @param id
     * @return
     */
    List<BidInfo> selectBidInfoListByProductId(Integer id);

    /**
     * 根据用户标识分页查询投资记录(包含产品详情)
     * @param paramMap
     * @return
     */
    List<BidInfo> selectBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 查询用户的所有投资记录条数
     * @param paramMap
     * @return
     */
    Long selectTotal(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取当前产品的所有投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectBidInfoByProductId(Integer loanId);
}