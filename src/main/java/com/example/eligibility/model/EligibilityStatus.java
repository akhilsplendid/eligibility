package com.example.eligibility.model;

public enum EligibilityStatus {
    APPROVED("Eligibility approved"),
    DENIED("Eligibility denied"),
    PENDING_REVIEW("Pending manual review"),
    INSUFFICIENT_DATA("Insufficient data for determination"),
    EXPIRED("PNR has expired"),
    INVALID("Invalid PNR format");

    private final String description;

    EligibilityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
