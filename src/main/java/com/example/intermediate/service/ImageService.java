package com.example.intermediate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Client amazonS3Client;

    // @Value("${cloud.aws.s3.bucket}")
    private String bucketName = "hanghae7zo";

    @Transactional
    public ResponseDto<?> uploadFileV1(MultipartFile multipartFile){
        try {
            validateFileExists(multipartFile);
        }catch (Exception e){
            return ResponseDto.fail("FILE_EMPTY", "파일이 비었습니다.");
        }

        String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        try (InputStream inputStream = multipartFile.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            objectMetadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicReadWrite));
        } catch (IOException e) {
            return ResponseDto.fail("BAD_TRANSLATION", "변환에 실패했습니다.");
        }
        String imgName;
        try {
            imgName = amazonS3Client.getUrl(bucketName, fileName).toString();
        }catch (Exception e){
            return ResponseDto.fail("UPLOAD_fAIL", "업로드에 실패했습니다.");
        }
        return ResponseDto.success(imgName);
    }

    private void validateFileExists(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IOException();
        }
    }
}
