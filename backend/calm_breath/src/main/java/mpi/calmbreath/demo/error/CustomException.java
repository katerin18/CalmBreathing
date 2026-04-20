package mpi.calmbreath.demo.error;

import lombok.*;

/**
 * Пользовательское исключение приложения
 */
@Getter
@Setter
public class CustomException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public CustomException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public CustomException(String message, Throwable cause, String errorCode, int httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
