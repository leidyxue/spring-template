package com.bfd.casejoin.exception;

import com.bfd.casejoin.common.ReturnMessage;

public class InvalidParamException extends BaseException {

    private static final long serialVersionUID = 3592214270861924752L;

    private String code;

    public InvalidParamException(String msg) {
        super(msg);
    }

    public InvalidParamException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code != null ? code : ReturnMessage.ParamError;
    }
}
