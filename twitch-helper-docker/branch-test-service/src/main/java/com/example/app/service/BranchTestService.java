package com.example.app.service;

import com.example.app.dto.FeatureFirstResponse;
import com.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BranchTestService {
    private final UserRepository userRepository;

    public BranchTestService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FeatureFirstResponse getFeatureFirstMessage() {
        return new FeatureFirstResponse("I am feature first message!");
    }
}