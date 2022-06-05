package com.babel.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ForeignApiService {
    @Bean
    public WebClient googleApiClient() {
        return WebClient.create("https://www.googleapis.com/books/v1");
    }

    @Bean
    public WebClient libGenApiClient() {
        return WebClient.create("http://libgen.rs");
    }
}
