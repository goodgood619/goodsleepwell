package com.example.goodsleepwell;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OutSideApiTest {
    public static void main(String[] args) throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("link", Collections.singletonList("https://www.youtube.com/watch?v=RcWN4RdnxDQ"));
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);
        Object ret = rt.postForEntity("http://localhost:3000/outsideApi", request, String.class);

        Thread.sleep(10000);
    }
}

