package com.bit.datainkback.common;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bit.datainkback.config.NaverConfiguration;
import com.bit.datainkback.dto.NoticeFileDto;
import com.bit.datainkback.dto.UserDetailDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

    @Component
    public class FileUtils {
        private final AmazonS3 s3;

        @Value("${naver.cloud.bucket.name}")
        private String bucketName;

        public FileUtils(NaverConfiguration naverConfiguration) {
            s3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    naverConfiguration.getEndPoint(),
                                    naverConfiguration.getRegionName()
                            )
                    )
                    .withCredentials(
                            new AWSStaticCredentialsProvider(
                                    new BasicAWSCredentials(
                                            naverConfiguration.getAccessKey(),
                                            naverConfiguration.getSecretKey()
                                    )
                            )
                    )
                    .build();
        }

        public NoticeFileDto parserFileInfo(MultipartFile multipartFile, String directory) {
            String bucketName = "dataink";

            NoticeFileDto noticeFileDto = new NoticeFileDto();

            // 다른 사용자가 같은 파일명의 파일을 업로드 했을 때
            // 덮어써지는 것을 방지하기 위해서 파일명을 랜덤값_날짜시간_파일명으로 지정
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date nowDate = new Date();

            String nowDateStr = format.format(nowDate);

            UUID uuid = UUID.randomUUID();

            String fileName =  uuid.toString() + "_" + nowDateStr + "_" + multipartFile.getOriginalFilename();

            // Object Storage에 파일 업로드
            try(InputStream fileInputStream = multipartFile.getInputStream()) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());

                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName,
                        directory + fileName,
                        fileInputStream,
                        objectMetadata
                ).withCannedAcl(CannedAccessControlList.PublicRead);

                s3.putObject(putObjectRequest);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            File uploadFile = new File(directory + fileName);
            String type = "";

            try {
                type = Files.probeContentType(uploadFile.toPath());
            } catch(IOException ie) {
                System.out.println(ie.getMessage());
            }

            if(!type.equals("")) {
                if(type.startsWith("image")) {
                    noticeFileDto.setFileType("image");
                } else {
                    noticeFileDto.setFileType("etc");
                }
            } else {
                noticeFileDto.setFileType("etc");
            }

            noticeFileDto.setFileName(fileName);
            noticeFileDto.setFileOriginName(multipartFile.getOriginalFilename());
            noticeFileDto.setFilePath(directory);

            return noticeFileDto;
        }

        public void deleteFile(String directory, String fileName) {
            String bucketName = "dataink";

            s3.deleteObject(new DeleteObjectRequest(bucketName, directory + fileName));
        }
    }


        // 마이페이지 관련 프로필 및 배경화면 이미지
//        private UserDetailDto uploadUserImage(MultipartFile multipartFile, String directory, boolean isProfile) {
//            UserDetailDto userDetailDto = new UserDetailDto();
//
//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//            Date nowDate = new Date();
//            String nowDateStr = format.format(nowDate);
//            UUID uuid = UUID.randomUUID();
//            String fileName = uuid.toString() + "_" + nowDateStr + "_" + multipartFile.getOriginalFilename();
//
//            // Object Storage에 파일 업로드
//            try(InputStream fileInputStream = multipartFile.getInputStream()) {
//                ObjectMetadata objectMetadata = new ObjectMetadata();
//                objectMetadata.setContentType(multipartFile.getContentType());
//
//                PutObjectRequest putObjectRequest = new PutObjectRequest(
//                        bucketName,
//                        directory + fileName,
//                        fileInputStream,
//                        objectMetadata
//                ).withCannedAcl(CannedAccessControlList.PublicRead);
//
//                s3.putObject(putObjectRequest);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//
//            File uploadFile = new File(directory + fileName);
//            String type = "";
//
//            try {
//                type = Files.probeContentType(uploadFile.toPath());
//            } catch(IOException ie) {
//                System.out.println(ie.getMessage());
//            }
//
//            if(!type.equals("")) {
//                if(type.startsWith("image")) {
//                    userDetailDto.setFiletype("image");
//                } else {
//                    userDetailDto.setFiletype("etc");
//                }
//            } else {
//                userDetailDto.setFiletype("etc");
//            }
//
//
//            // 파일 업로드
//            try (InputStream fileInputStream = multipartFile.getInputStream()) {
//                uploadFileToS3(directory, fileName, fileInputStream, multipartFile.getContentType());
//            } catch (Exception e) {
//                System.out.println("File upload failed: " + e.getMessage());
//            }
//
//            // DTO에 정보 설정
//            if (isProfile) {
//                userDetailDto.setProfileImgName(fileName);
//                userDetailDto.setProfileImgOriginname(multipartFile.getOriginalFilename());
//                userDetailDto.setProfileImageUrl(directory + fileName);
//                userDetailDto.setProfileImgType("image"); // 타입 설정 (간단화)
//            } else {
//                userDetailDto.setBackgroundImgName(fileName);
//                userDetailDto.setBackgroundImgOriginname(multipartFile.getOriginalFilename());
//                userDetailDto.setBackgroundImageUrl(directory + fileName);
//                userDetailDto.setBackgroundImgType("image"); // 타입 설정 (간단화)
//            }
//
//            return userDetailDto;
//        }
//
//        public String uploadFile(String directory, MultipartFile multipartFile) {
//            String fileName = generateFileName(multipartFile.getOriginalFilename());
//            try (InputStream fileInputStream = multipartFile.getInputStream()) {
//                uploadFileToS3(directory, fileName, fileInputStream, multipartFile.getContentType());
//            } catch (Exception e) {
//                System.out.println("File upload failed: " + e.getMessage());
//            }
//            return fileName;
//        }
//
//        public String getFileUrl(String directory, String fileName) {
//            return s3.getUrl(bucketName, directory + fileName).toString();
//        }
//
//
//        private void uploadFileToS3(String directory, String fileName, InputStream fileInputStream, String contentType) throws IOException {
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentType(contentType);
//
//            PutObjectRequest putObjectRequest = new PutObjectRequest(
//                    bucketName,
//                    directory + fileName,
//                    fileInputStream,
//                    objectMetadata
//            ).withCannedAcl(CannedAccessControlList.PublicRead);
//
//            s3.putObject(putObjectRequest);
//        }
//    }
