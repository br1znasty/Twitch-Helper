package com.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeatureSecondResponse {
    private String SecondMessage;

    private String pashalko = "UgaBuga"; 

    public String getPashalko() {
        return pashalko;
    }
}