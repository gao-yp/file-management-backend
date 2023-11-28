package com.gyp.jx.file.management.config;


import com.gyp.jx.file.management.config.error.BizException;
import com.gyp.jx.file.management.config.error.CommonEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("com.gyp.jx.gypnas.controller.api")
@Log4j2
public class GlobalApiExceptionHandler {

    /**
     * 处理自定义的业务异常
     *
     * @param e 自定义的业务异常
     * @return ResultBody
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultBody exceptionHandler(BizException e) {
        log.error("发生业务异常！原因是：{}", e.getErrorMsg());
        return ResultBody.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     *
     * @param e 空指针的异常
     * @return ResultBody
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultBody exceptionHandler(NullPointerException e) {
        log.error("发生空指针异常！原因是:", e);
        return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * @param e 响应异常
     * @return ResultBody
     */
    @ExceptionHandler(value = ErrorResponseException.class)
    @ResponseBody
    public ResultBody exceptionHandler(ErrorResponseException e) {
        log.error("请求错误！原因是：{}", e.getMessage());
        return ResultBody.error(e.getStatusCode());
    }

    /**
     * 未知异常
     *
     * @param e 未知异常
     * @return ResultBody
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler( Exception e) {
        log.error("未知异常！原因是:", e);
        return ResultBody.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}

