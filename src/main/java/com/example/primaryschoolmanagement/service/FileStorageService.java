package com.example.primaryschoolmanagement.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 存储头像文件
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件访问URL
     */
    String storeAvatar(MultipartFile file, Long userId);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 验证图片文件
     *
     * @param file 上传的文件
     * @throws IllegalArgumentException 如果文件不符合要求
     */
    void validateImageFile(MultipartFile file);
}
