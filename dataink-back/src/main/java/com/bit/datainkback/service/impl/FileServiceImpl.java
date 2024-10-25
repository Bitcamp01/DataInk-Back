package com.bit.datainkback.service.impl;

import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private final FileUtils fileUtils;


    @Override
    public String uploadFile(MultipartFile file, String directory) {
        return fileUtils.parserFileInfoToProject(file, directory);
    }



    @Override
    public void deleteFile(String directory, String fileName) {
        // 파일 삭제
        fileUtils.deleteFile(directory, fileName);
    }

    @Override
    public void copyFile(String originalName, String copyName) {
        fileUtils.copyFileInS3(originalName, copyName);
    }


}
