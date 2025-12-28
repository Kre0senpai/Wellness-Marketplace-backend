package com.wellness.wellness_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OpenFdaService {

    private static final String OPEN_FDA_URL =
            "https://api.fda.gov/drug/label.json?search=indications_and_usage:";

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchDrugInfo(String symptom) {

        try {
            String url = OPEN_FDA_URL + symptom;
            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("results")) {
                return "No FDA data found";
            }

            return "FDA data available for symptom: " + symptom;

        } catch (Exception e) {
            return "FDA data not available";
        }
    }
}
