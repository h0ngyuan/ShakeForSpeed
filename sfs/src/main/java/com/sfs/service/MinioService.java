package com.sfs.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final com.sfs.config.MinioConfig minioConfig;

    public String uploadFile(InputStream stream, String fileName, String contentType) throws Exception {
        ObjectWriteResponse response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(fileName)
                        .stream(stream, -1, 10 * 1024 * 1024)
                        .contentType(contentType)
                        .build()
        );
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + response.object();
    }
}
