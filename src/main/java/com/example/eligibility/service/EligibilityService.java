package com.example.eligibility.service;

import com.example.eligibility.model.EligibilityResponse;
import com.example.eligibility.model.EligibilityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EligibilityService {

    private static final Logger logger = LoggerFactory.getLogger(EligibilityService.class);
    private static final Pattern PNR_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

    @Cacheable(value = "eligibility", key = "#pnr")
    public EligibilityResponse checkEligibility(String pnr) {
        logger.info("Computing eligibility for PNR: {}", pnr);

        // Simulate expensive computation (database lookup, external API call, etc.)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Eligibility computation interrupted for PNR: {}", pnr, e);
            return new EligibilityResponse(pnr, false, EligibilityStatus.INSUFFICIENT_DATA,
                    "Computation was interrupted");
        }

        // Validate PNR format
        if (!isValidPnrFormat(pnr)) {
            logger.warn("Invalid PNR format: {}", pnr);
            return new EligibilityResponse(pnr, false, EligibilityStatus.INVALID,
                    "PNR must be 6 alphanumeric characters");
        }

        // Business logic for eligibility determination
        EligibilityResponse response = determineEligibility(pnr);
        logger.info("Eligibility determined for PNR {}: {}", pnr, response.getStatus());

        return response;
    }

    private boolean isValidPnrFormat(String pnr) {
        return pnr != null && PNR_PATTERN.matcher(pnr).matches();
    }

    private EligibilityResponse determineEligibility(String pnr) {
        // Business rules for eligibility
        // Rule 1: PNRs starting with 'X' are expired
        if (pnr.startsWith("X")) {
            return new EligibilityResponse(pnr, false, EligibilityStatus.EXPIRED,
                    "PNR has expired");
        }

        // Rule 2: PNRs starting with 'D' are denied
        if (pnr.startsWith("D")) {
            return new EligibilityResponse(pnr, false, EligibilityStatus.DENIED,
                    "PNR is on the deny list");
        }

        // Rule 3: PNRs starting with 'P' require manual review
        if (pnr.startsWith("P")) {
            return new EligibilityResponse(pnr, false, EligibilityStatus.PENDING_REVIEW,
                    "PNR requires manual review");
        }

        // Rule 4: PNRs with less than 3 digits are insufficient
        long digitCount = pnr.chars().filter(Character::isDigit).count();
        if (digitCount < 3) {
            return new EligibilityResponse(pnr, false, EligibilityStatus.INSUFFICIENT_DATA,
                    "Insufficient booking data");
        }

        // Default: Approved
        return new EligibilityResponse(pnr, true, EligibilityStatus.APPROVED,
                "All eligibility criteria met");
    }
}
