package com.example.primaryschoolmanagement.service.impl;

import com.example.primaryschoolmanagement.common.exception.BusinessException;
import com.example.primaryschoolmanagement.config.FileUploadProperties;
import com.example.primaryschoolmanagement.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            // 初始化上传根目录
            uploadPath = Paths.get(fileUploadProperties.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("文件上传目录初始化成功：{}", uploadPath);
        } catch (IOException e) {
            log.error("无法创建上传目录：{}", fileUploadProperties.getUploadDir(), e);
            throw new BusinessException("无法创建文件上传目录");
        }
    }

    @Override
    public String storeAvatar(MultipartFile file, Long userId) {
        // 1. 验证文件
        validateImageFile(file);

        // 2. 生成文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = generateFilename(userId, fileExtension);

        try {
            // 3. 创建按日期分类的子目录：avatars/2024/01/15/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path targetDir = uploadPath.resolve(fileUploadProperties.getAvatarDir()).resolve(datePath);
            Files.createDirectories(targetDir);

            // 4. 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 5. 生成访问URL
            String fileUrl = String.format("%s/%s/%s/%s",
                    fileUploadProperties.getAccessUrlPrefix(),
                    fileUploadProperties.getAvatarDir(),
                    datePath,
                    newFilename);

            log.info("文件上传成功：userId={}, fileUrl={}", userId, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件存储失败：userId={}, filename={}", userId, newFilename, e);
            throw new BusinessException("文件存储失败");
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }

        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace(fileUploadProperties.getAccessUrlPrefix() + "/", "");
            Path filePath = uploadPath.resolve(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功：{}", fileUrl);
                return true;
            } else {
                log.warn("文件不存在：{}", fileUrl);
                return false;
            }
        } catch (IOException e) {
            log.error("文件删除失败：{}", fileUrl, e);
            return false;
        }
    }

    @Override
    public void validateImageFile(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 2. 检查文件大小
        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            throw new IllegalArgumentException(
                    String.format("文件大小不能超过 %d MB", fileUploadProperties.getMaxFileSize() / 1024 / 1024)
            );
        }

        // 3. 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new IllegalArgumentException("只允许上传图片文件（JPG、PNG、GIF）");
        }

        // 4. 检查文件名
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new IllegalArgumentException("文件名不合法");
        }
    }

    @Override
    public String storeCover(MultipartFile file, Long subjectId) {
        // 1. 验证文件（复用头像的图片验证逻辑：非空、大小、类型、文件名合法性）
        validateImageFile(file);

        // 2. 生成唯一文件名（规则：subjectId_UUID.扩展名，和头像风格一致）
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = generateCoverFilename(subjectId, fileExtension);

        try {
            // 3. 创建按日期分类的子目录（格式：subject-covers/2024/01/15/，和头像目录结构一致）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            // 根目录/upload + 封面子目录subject-covers + 日期目录
            Path targetDir = uploadPath.resolve(fileUploadProperties.getCoverDir()).resolve(datePath);
            Files.createDirectories(targetDir); // 不存在则创建多级目录

            // 4. 保存文件（覆盖已存在的同名文件，避免冲突）
            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 5. 生成访问URL（格式：/upload/subject-covers/2024/01/15/subjectId_UUID.jpg）
            String fileUrl = String.format("%s/%s/%s/%s",
                    fileUploadProperties.getAccessUrlPrefix(), // 前缀：/upload
                    fileUploadProperties.getCoverDir(), // 封面目录：subject-covers
                    datePath, // 日期目录：2024/01/15
                    newFilename // 文件名：1_123e4567e89b12d3a456.jpg
            );

            log.info("科目封面上传成功：subjectId={}, fileUrl={}", subjectId, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("科目封面存储失败：subjectId={}, filename={}", subjectId, newFilename, e);
            throw new BusinessException("科目封面上传失败");
        }
    }

    /**
     * 新增：生成科目封面唯一文件名（规则：subjectId_UUID.扩展名）
     */
    private String generateCoverFilename(Long subjectId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 生成无横线UUID
        return String.format("%d_%s%s", subjectId, uuid, extension); // 示例：1_123e4567e89b12d3a456.jpg
    }


    /**
     * 检查是否为允许的图片类型
     */
    private boolean isAllowedImageType(String contentType) {
        return Arrays.asList(fileUploadProperties.getAllowedImageTypes()).contains(contentType);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成唯一文件名：userId_UUID.ext
     */
    private String generateFilename(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%d_%s%s", userId, uuid, extension);
    }
}
