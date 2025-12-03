package com.zonagamer.zonagamer_backend.service;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    
    private final Storage storage;
    
    @Value("${firebase.storage-bucket}")
    private String bucketName;
    
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType())
            .build();
            
        storage.create(blobInfo, file.getBytes());
        
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
    
    public void deleteFile(String fileUrl) {
        String fileName = extractFileNameFromUrl(fileUrl);
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }
    
    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}