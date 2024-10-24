package com.bit.datainkback.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void uploadFile(MultipartFile file, String directory);
    void deleteFile(String directory, String fileName);
}
