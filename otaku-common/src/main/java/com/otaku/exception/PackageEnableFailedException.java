package com.otaku.exception;

/**
 * 套餐启用失败异常
 */
public class PackageEnableFailedException extends BaseException {

    public PackageEnableFailedException(){}

    public PackageEnableFailedException(String msg){
        super(msg);
    }
}
