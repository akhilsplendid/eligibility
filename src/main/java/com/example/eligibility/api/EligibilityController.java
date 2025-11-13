package com.example.eligibility.api;

import com.example.eligibility.model.EligibilityResponse;
import com.example.eligibility.service.EligibilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private static final Logger logger = LoggerFactory.getLogger(EligibilityController.class);
    private final EligibilityService eligibilityService;

    @Autowired
    public EligibilityController(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/{pnr}")
    public ResponseEntity<EligibilityResponse> checkEligibility(@PathVariable String pnr) {
        logger.debug("Received eligibility check request for PNR: {}", pnr);

        EligibilityResponse response = eligibilityService.checkEligibility(pnr.toUpperCase());

        if (response.isEligible()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response); // Return 200 with eligibility details
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Eligibility service is running");
    }
}

