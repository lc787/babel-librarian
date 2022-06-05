package com.babel.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class GoogleApiService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    private final WebClient googleApiClient;

    @Autowired
    public GoogleApiService(WebClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }


    public JSONObject getFirstBookQueue(String bookName) {
        JSONObject json = googleApiClient
                .get()
                .uri("/volumes?q=" + bookName)
                .retrieve()
                .bodyToMono(JSONObject.class) //This doesn't work
                .block(REQUEST_TIMEOUT);

        System.out.println(json.toString());
        return json;
    }


}
