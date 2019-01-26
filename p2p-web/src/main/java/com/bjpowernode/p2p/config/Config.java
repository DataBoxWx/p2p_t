package com.bjpowernode.p2p.config;

/**
 * ClassName:Config
 * package:com.bjpowernode.p2p.config
 * Descrption:
 *
 * @Date:2018/7/14 9:20
 * @Author:guoxin
 */
public class Config {

    /**
     * 实名认证接口的key
     */
    private String realName_appkey;

    /**
     * 实名认证接口url
     */
    private String realName_url;

    private String p2p_pay_alipay_url;

    private String p2p_pay_alipay_query_url;

    public String getP2p_pay_alipay_query_url() {
        return p2p_pay_alipay_query_url;
    }

    public void setP2p_pay_alipay_query_url(String p2p_pay_alipay_query_url) {
        this.p2p_pay_alipay_query_url = p2p_pay_alipay_query_url;
    }

    public String getP2p_pay_alipay_url() {
        return p2p_pay_alipay_url;
    }

    public void setP2p_pay_alipay_url(String p2p_pay_alipay_url) {
        this.p2p_pay_alipay_url = p2p_pay_alipay_url;
    }

    public String getRealName_appkey() {
        return realName_appkey;
    }

    public void setRealName_appkey(String realName_appkey) {
        this.realName_appkey = realName_appkey;
    }

    public String getRealName_url() {
        return realName_url;
    }

    public void setRealName_url(String realName_url) {
        this.realName_url = realName_url;
    }
}
