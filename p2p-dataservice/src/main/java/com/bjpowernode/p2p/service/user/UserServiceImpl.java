package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:UserServiceImpl
 * package:com.bjpowernode.p2p.service.user
 * Descrption:
 *
 * @Date:2018/7/12 9:04
 * @Author:guoxin
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Long queryAllUserCount() {
        //设置缓存中key值的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //首先去redis缓存中查询,有:直接使用,没有:去数据库查询

        //获取操作指定key的操作对象
        BoundValueOperations<Object, Object> boundValueOps = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);

        //获取该操作对象的值
        Long allUserCount = (Long) boundValueOps.get();

        //判断是否有值
        if (null == allUserCount) {
            //没有值:去数据库查询
            allUserCount = userMapper.selectAllUserCount();

            //将该值存放到redis缓存中
//            boundValueOps.set(allUserCount,15, TimeUnit.MINUTES);

            //将指定值存放到redis的key中
            boundValueOps.set(allUserCount);
            //设置失效时间
            boundValueOps.expire(15,TimeUnit.MINUTES);
        }

        return allUserCount;
    }


    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        resultObject.setErrorMessage("注册成功");


        //新增用户
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int insertUserCount = userMapper.insertSelective(user);

        if (insertUserCount > 0) {
            User userInfo = userMapper.selectUserByPhone(phone);

            //开立帐户
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(userInfo.getId());
            financeAccount.setAvailableMoney(888.0);
            int insertCount = financeAccountMapper.insertSelective(financeAccount);

            if (insertCount <= 0) {
                resultObject.setErrorCode(Constants.FAIL);
                resultObject.setErrorMessage("注册失败");
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
            resultObject.setErrorMessage("注册失败");
        }
        return resultObject;
    }


    @Override
    public int modifyUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }


    @Override
    public User login(String phone, String loginPassword) {

        //根据手机号和登录密码查询用户
        User user = userMapper.selectUserByPhoneAndLoginPassword(phone,loginPassword);

        //判断用户是否为空
        if (null != user) {
            //更新用户的登录时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);

        }

        return user;
    }
}













