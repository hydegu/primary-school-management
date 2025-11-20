package com.example.primaryschoolmanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 文件上传根目录
     */
    private String uploadDir = "uploads";

    /**
     * 头像文件存储子目录
     */
    private String avatarDir = "avatars";

    /**
     * 允许的图片格式
     */
    private String[] allowedImageTypes = {"image/jpeg", "image/png", "image/jpg", "image/gif"};

    /**
     * 文件最大大小（字节），默认 5MB
     */
    private long maxFileSize = 5 * 1024 * 1024;

    /**
     * 文件访问URL前缀
     */
    private String accessUrlPrefix = "/uploads";
}
