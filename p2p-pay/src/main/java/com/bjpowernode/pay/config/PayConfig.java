package com.bjpowernode.pay.config;

/**
 * ClassName:PayConfig
 * package:com.bjpowernode.pay.config
 * Descrption:
 *
 * @Date:2018/7/17 14:58
 * @Author:guoxin
 */
public class PayConfig {

    private String alipay_app_id;
    private String alipay_method;
    private String alipay_format;
    private String alipay_charset;
    private String alipay_sign_type;
    private String alipay_version;
    private String alipay_gatewayUrl;
    private String merchant_private_key;
    private String alipay_public_key;
    private String alipay_return_url;
    private String alipay_notify_url;
    private String pay_p2p_return_url;

    public String getPay_p2p_return_url() {
        return pay_p2p_return_url;
    }

    public void setPay_p2p_return_url(String pay_p2p_return_url) {
        this.pay_p2p_return_url = pay_p2p_return_url;
    }

    public String getAlipay_return_url() {
        return alipay_return_url;
    }

    public void setAlipay_return_url(String alipay_return_url) {
        this.alipay_return_url = alipay_return_url;
    }

    public String getAlipay_notify_url() {
        return alipay_notify_url;
    }

    public void setAlipay_notify_url(String alipay_notify_url) {
        this.alipay_notify_url = alipay_notify_url;
    }

    public String getAlipay_app_id() {
        return alipay_app_id;
    }

    public void setAlipay_app_id(String alipay_app_id) {
        this.alipay_app_id = alipay_app_id;
    }

    public String getAlipay_method() {
        return alipay_method;
    }

    public void setAlipay_method(String alipay_method) {
        this.alipay_method = alipay_method;
    }

    public String getAlipay_format() {
        return alipay_format;
    }

    public void setAlipay_format(String alipay_format) {
        this.alipay_format = alipay_format;
    }

    public String getAlipay_charset() {
        return alipay_charset;
    }

    public void setAlipay_charset(String alipay_charset) {
        this.alipay_charset = alipay_charset;
    }

    public String getAlipay_sign_type() {
        return alipay_sign_type;
    }

    public void setAlipay_sign_type(String alipay_sign_type) {
        this.alipay_sign_type = alipay_sign_type;
    }

    public String getAlipay_version() {
        return alipay_version;
    }

    public void setAlipay_version(String alipay_version) {
        this.alipay_version = alipay_version;
    }

    public String getAlipay_gatewayUrl() {
        return alipay_gatewayUrl;
    }

    public void setAlipay_gatewayUrl(String alipay_gatewayUrl) {
        this.alipay_gatewayUrl = alipay_gatewayUrl;
    }

    public String getMerchant_private_key() {
        return merchant_private_key;
    }

    public void setMerchant_private_key(String merchant_private_key) {
        this.merchant_private_key = merchant_private_key;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }
}
