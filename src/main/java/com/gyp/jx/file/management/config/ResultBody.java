package com.gyp.jx.file.management.config;


import com.alibaba.fastjson2.JSON;
import com.gyp.jx.file.management.config.error.BaseErrorInfoInterface;
import com.gyp.jx.file.management.config.error.CommonEnum;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ResultBody {
    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object result;

    private ResultBody() {
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 成功
     */
    public static ResultBody success() {
        return success(null);
    }

    /**
     * 成功
     */
    public static ResultBody success(Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMessage(CommonEnum.SUCCESS.getResultMsg());
        rb.setResult(data);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultBody error(BaseErrorInfoInterface errorInfo) {
        ResultBody rb = new ResultBody();
        rb.setCode(errorInfo.getResultCode());
        rb.setMessage(errorInfo.getResultMsg());
        rb.setResult(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultBody error(String code, String message) {
        ResultBody rb = new ResultBody();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    public static ResultBody error(int code, String message) {
        ResultBody rb = new ResultBody();
        rb.setCode(String.valueOf(code));
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    public static ResultBody error(HttpStatusCode httpStatus) {
        ResultBody rb = new ResultBody();
        rb.setCode(String.valueOf(httpStatus.value()));
        rb.setMessage(httpStatus.toString());
        rb.setResult(null);
        return rb;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
