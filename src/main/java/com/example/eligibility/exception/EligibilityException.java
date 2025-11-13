package com.example.eligibility.exception;

public class EligibilityException extends RuntimeException {
    private final String errorCode;

    public EligibilityException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EligibilityException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
