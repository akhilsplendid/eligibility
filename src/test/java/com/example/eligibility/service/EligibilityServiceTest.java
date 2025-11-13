package com.example.eligibility.service;

import com.example.eligibility.model.EligibilityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EligibilityServiceTest {

    @Autowired
    private EligibilityService eligibilityService;

    @Test
    void testApprovedEligibility() {
        var response = eligibilityService.checkEligibility("ABC123");
        assertThat(response.isEligible()).isTrue();
        assertThat(response.getStatus()).isEqualTo(EligibilityStatus.APPROVED);
    }

    @Test
    void testDeniedEligibility() {
        var response = eligibilityService.checkEligibility("DENIED");
        assertThat(response.isEligible()).isFalse();
        assertThat(response.getStatus()).isEqualTo(EligibilityStatus.DENIED);
    }

    @Test
    void testExpiredPnr() {
        var response = eligibilityService.checkEligibility("X12345");
        assertThat(response.isEligible()).isFalse();
        assertThat(response.getStatus()).isEqualTo(EligibilityStatus.EXPIRED);
    }

    @Test
    void testPendingReview() {
        var response = eligibilityService.checkEligibility("P12345");
        assertThat(response.isEligible()).isFalse();
        assertThat(response.getStatus()).isEqualTo(EligibilityStatus.PENDING_REVIEW);
    }

    @Test
    void testInvalidPnrFormat() {
        var response = eligibilityService.checkEligibility("ABC");
        assertThat(response.isEligible()).isFalse();
        assertThat(response.getStatus()).isEqualTo(EligibilityStatus.INVALID);
    }

    @Test
    void testCachingWorks() {
        String pnr = "TEST99";

        // First call - should compute
        var response1 = eligibilityService.checkEligibility(pnr);
        var timestamp1 = response1.getComputedAt();

        // Second call - should be cached (timestamp should be the same)
        var response2 = eligibilityService.checkEligibility(pnr);
        var timestamp2 = response2.getComputedAt();

        assertThat(timestamp1).isEqualTo(timestamp2);
        assertThat(response1.getPnr()).isEqualTo(response2.getPnr());
    }
}
