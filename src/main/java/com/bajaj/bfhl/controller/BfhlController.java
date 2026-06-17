package com.bajaj.bfhl.controller;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;
import com.bajaj.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private static final Logger log = LoggerFactory.getLogger(BfhlController.class);
    private final BfhlService bfhlService;

    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }

    @PostMapping
    public ResponseEntity<BfhlResponse> process(
            @Valid @RequestBody BfhlRequest request,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @RequestHeader(value = "mobile-no", required = false) String mobileNo) {

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
            log.info("No X-Request-Id provided, generated: {}", requestId);
        }

        BfhlResponse response = bfhlService.process(request, requestId);
        response.setMobile_no(mobileNo);
        return ResponseEntity.ok()
                .header("X-Request-Id", requestId)
                .body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "bfhl-api",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
