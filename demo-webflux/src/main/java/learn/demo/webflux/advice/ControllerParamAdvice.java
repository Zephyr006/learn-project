package learn.demo.webflux.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Optional;

/**
 * Controller的异常处理切面
 * @author Zephyr
 * @since 2020-11-22.
 */
@ControllerAdvice
public class ControllerParamAdvice {

    /**
     * 处理Controller参数校验的错误
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleBindException(WebExchangeBindException e) {
        //List<FieldError> fieldErrors = e.getFieldErrors();
        return new ResponseEntity<>(toErrorString(e), HttpStatus.BAD_REQUEST);
    }

    private <T> String toErrorString(Errors e) {
        Optional<String> errorMsgOpt = e.getFieldErrors().stream()
                .map(ex -> String.format("%s 的 %s %s", ex.getObjectName(), ex.getField(), ex.getDefaultMessage()))
                // 作用：合并、汇总为一条数据
                .reduce((s1, s2) -> s1 + "\n" + s2);
        return errorMsgOpt.orElse("Error: empty error message.");
    }
}
