package com.example.app.controller;

import com.example.app.dto.FeatureSecondResponse;
import com.example.app.service.BranchTestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-branch")
public class BranchTestController {
    private final BranchTestService branchTestService;

    public BranchTestController(BranchTestService branchTestService) {
        this.branchTestService = branchTestService;
    }

    @GetMapping("/feature-second")
    public FeatureFirstResponse getFeatureSecondMessage() {
        return branchTestService.getFeatureSecondMessage();
    }
}