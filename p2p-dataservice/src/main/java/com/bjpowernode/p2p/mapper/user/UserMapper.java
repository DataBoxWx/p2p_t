package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.User;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    User selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_user
     *
     * @mbggenerated Tue Jul 10 11:25:39 CST 2018
     */
    int updateByPrimaryKey(User record);

    /**
     * 获取平台注册总人数
     * @return
     */
    Long selectAllUserCount();

    /**
     * 根据手机号查询用户信息
     * @param phone
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 根据用户手机号和登录密码查询用户
     * @param phone
     * @param loginPassword
     * @return
     */
    User selectUserByPhoneAndLoginPassword(String phone, String loginPassword);
}