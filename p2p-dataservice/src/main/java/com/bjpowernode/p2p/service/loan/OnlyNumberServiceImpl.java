package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName:OnlyNumberServiceImpl
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/17 12:25
 * @Author:guoxin
 */
@Service("onlyNumberServiceImpl")
public class OnlyNumberServiceImpl implements OnlyNumberService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Long getOnlyNumber() {
        return redisTemplate.opsForValue().increment(Constants.ONLY_NUMBER,1);
    }
}
