package com.localhub.localhub.service;

import com.localhub.localhub.r2.R2StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlResolver {

    private final R2StorageService r2StorageService;


    public String toPresignedUrl(String key) {

        if(key == null || key.isBlank()) return null;
        return r2StorageService.presignGet(key, 3600).toString();

    }

}
