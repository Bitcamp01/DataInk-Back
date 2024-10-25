package com.bit.datainkback.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String directory);
    void deleteFile(String directory, String fileName);

    void copyFile(String label, String uniqueName);
}
