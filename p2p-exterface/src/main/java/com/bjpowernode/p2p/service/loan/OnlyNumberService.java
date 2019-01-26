package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:OnlyNumberService
 * package:com.bjpowernode.p2p.service.loan
 * Descrption:
 *
 * @Date:2018/7/17 12:25
 * @Author:guoxin
 */
public interface OnlyNumberService {
    /**
     * 获取redis全局唯一数字
     * @return
     */
    Long getOnlyNumber();
}
