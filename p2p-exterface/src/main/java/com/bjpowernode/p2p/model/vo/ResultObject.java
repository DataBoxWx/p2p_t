package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;

/**
 * ClassName:ResultObject
 * package:com.bjpowernode.p2p.model.vo
 * Descrption:
 *
 * @Date:2018/7/13 11:59
 * @Author:guoxin
 */
public class ResultObject implements Serializable{

    /**
     * 错误码:SUCCESS|FAIL
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
