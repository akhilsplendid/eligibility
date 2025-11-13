package com.example.eligibility.model;

import java.time.Instant;

public class EligibilityResponse {
    private String pnr;
    private boolean eligible;
    private EligibilityStatus status;
    private String reason;
    private Instant computedAt;
    private boolean fromCache;

    public EligibilityResponse() {
    }

    public EligibilityResponse(String pnr, boolean eligible, EligibilityStatus status, String reason) {
        this.pnr = pnr;
        this.eligible = eligible;
        this.status = status;
        this.reason = reason;
        this.computedAt = Instant.now();
        this.fromCache = false;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public EligibilityStatus getStatus() {
        return status;
    }

    public void setStatus(EligibilityStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(Instant computedAt) {
        this.computedAt = computedAt;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
}
