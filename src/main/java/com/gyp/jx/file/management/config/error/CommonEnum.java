package com.gyp.jx.file.management.config.error;


public enum CommonEnum implements BaseErrorInfoInterface {
    // 数据操作错误定义
    SUCCESS("success", "成功!"),
    BODY_NOT_MATCH("file01", "请求的数据格式不符!"),
    INTERNAL_SERVER_ERROR("error", "服务器内部错误!"),
    ;

    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    CommonEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }

}
