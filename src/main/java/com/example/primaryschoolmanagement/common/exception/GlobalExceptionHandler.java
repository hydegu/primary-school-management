package com.example.primaryschoolmanagement.common.exception;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : "请求参数校验失败";
        log.warn("参数校验失败: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.er(ResultCode.BAD_REQUEST.getCode(),message));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<R> handleApiException(ApiException ex) {
        HttpStatus status = ex.getStatus();
        log.warn("业务异常: status={}, message={}", status.value(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(R.er(convertStatusToCode(status).getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R> handleOtherException(Exception ex) {
        log.error("未处理异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(R.er(ResultCode.ERROR.getCode(),ResultCode.ERROR.getMsg()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<R> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("参数异常", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.er(ResultCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }

    /**
     * 处理用户未找到异常
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<R> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("用户未找到异常：{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(R.er(ResultCode.NOT_FOUND.getCode(), ex.getMessage()));
    }

    /**
     * 处理角色未找到异常
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<R> handleRoleNotFoundException(RoleNotFoundException ex) {
        log.warn("角色未找到异常：{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(R.er(ResultCode.NOT_FOUND.getCode(), ex.getMessage()));
    }

    /**
     * 处理数据重复异常
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<R> handleDuplicateException(DuplicateException ex) {
        log.warn("数据重复异常：{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(R.er(ResultCode.CONFLICT.getCode(), ex.getMessage()));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R> handleBusinessException(BusinessException ex) {
        log.warn("业务异常：{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.er(ResultCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }

    private ResultCode convertStatusToCode(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> ResultCode.BAD_REQUEST;
            case UNAUTHORIZED -> ResultCode.UNAUTHORIZED;
            case FORBIDDEN -> ResultCode.UNAUTHORIZATION;
            case NOT_FOUND -> ResultCode.NOT_FOUND;
            case GONE -> ResultCode.GONE;
            case CONFLICT -> ResultCode.CONFLICT;
            default -> ResultCode.ERROR;
        };
    }
}
