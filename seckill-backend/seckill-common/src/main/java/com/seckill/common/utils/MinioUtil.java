package com.seckill.common.utils;

import io.minio.*;
import io.minio.http.Method;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Slf4j
public class MinioUtil implements InitializingBean {

    private MinioClient minioClient;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @Override
    public void afterPropertiesSet() {
        createMinioClient();
    }

    private void createMinioClient() {
        try {
            this.minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Minio初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Minio服务器异常");
        }
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 按日期分目录：2025/12/17/xxxx.png
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String originName = file.getOriginalFilename();
        String suffix = (StringUtils.hasText(originName) && originName.contains("."))
                ? originName.substring(originName.lastIndexOf("."))
                : "";

        String objectName = datePath + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("上传错误: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败");
        }

        return getUrl(objectName);
    }

    public String getUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件URL失败");
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("Minio服务器异常");
        }
    }
}
