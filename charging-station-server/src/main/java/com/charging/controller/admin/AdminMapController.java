package com.charging.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/map")
public class AdminMapController {

    @Value("${amap.key:}")
    private String amapKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/geocode")
    public Map<String, Object> geocode(
            @RequestParam String address,
            @RequestParam(defaultValue = "json") String output) {

        String url = "https://restapi.amap.com/v3/geocode/geo"
                + "?address=" + address
                + "&key=" + amapKey
                + "&output=" + output;

        return restTemplate.getForObject(url, Map.class);
    }
}
