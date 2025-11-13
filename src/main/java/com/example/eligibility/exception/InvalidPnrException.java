package com.example.eligibility.exception;

public class InvalidPnrException extends EligibilityException {
    public InvalidPnrException(String message) {
        super(message, "INVALID_PNR");
    }

    public InvalidPnrException(String pnr, String reason) {
        super(String.format("Invalid PNR '%s': %s", pnr, reason), "INVALID_PNR");
    }
}
