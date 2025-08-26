package com.sfs.config.MinioConfig;

import cn.hutool.core.lang.UUID;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


@Component
public class MinioService {
    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;

    public String uploadImage(MultipartFile file, String bucketLabel) throws Exception {
        String bucketName = minioConfig.getBuckets().get(bucketLabel);
        if (bucketName == null) {
            throw new IllegalArgumentException("未知的桶类型: " + bucketLabel);
        }

        createBucketIfNotExists(bucketName);

        String fileName = generateFileName(file);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        return minioConfig.getEndpoint() + "/" + bucketName + "/" + fileName;
    }

    /**
     * 删除 MinIO 中的文件
     * @param url 文件的完整URL，例如 http://localhost:9000/bucket/uploads/abc.png
     * @return 是否删除成功
     */
    public boolean deleteFile(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return false;
            }

            // 从URL 中提取bucketName 和objectName
            URI uri = new URI(url);
            String path = uri.getPath(); // 得到的值 /bucket-name/path/to/file.jpg
            String[] parts = path.split("/", 3); // 分成 [ "", bucketName, objectName ]

            if (parts.length < 3) {
                return false;
            }

            String bucketName = parts[1];
            String objectName = parts[2];

            // 执行删除
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String ext = file.getOriginalFilename();
        ext = ext.substring(ext.lastIndexOf("."));
        return UUID.randomUUID() + ext;
    }
}