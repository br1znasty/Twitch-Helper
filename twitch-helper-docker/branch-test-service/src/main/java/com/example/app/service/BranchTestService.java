package com.example.app.service;

import com.example.app.dto.FeatureSecondResponse;
import com.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BranchTestService {
    private final UserRepository userRepository;

    public BranchTestService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FeatureSecondResponse getFeatureFirstMessage() {
        return new FeatureSecondResponse("I am feature ssecond message!");
    }
}