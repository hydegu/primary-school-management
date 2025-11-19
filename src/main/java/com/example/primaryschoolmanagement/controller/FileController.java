package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 通用文件上传接口（暂时用于头像上传）
     *
     * @param file 上传的文件
     * @param userId 用户ID（可选，如果不传则使用当前登录用户ID）
     * @return 文件访问URL
     */
    @PostMapping("/upload/avatar")
    public R uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        log.info("接收到头像上传请求：userId={}, filename={}", userId, file.getOriginalFilename());

        try {
            // 如果没有传userId，可以从JWT token中获取（这里简化处理）
            if (userId == null) {
                // TODO: 从当前登录用户获取userId
                throw new IllegalArgumentException("用户ID不能为空");
            }

            // 存储文件
            String fileUrl = fileStorageService.storeAvatar(file, userId);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", file.getOriginalFilename());
            result.put("size", file.getSize());

            log.info("头像上传成功：userId={}, url={}", userId, fileUrl);
            return R.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("头像上传失败：{}", e.getMessage());
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            log.error("头像上传异常", e);
            return R.er(500, "文件上传失败");
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @DeleteMapping
    public R deleteFile(@RequestParam("url") String fileUrl) {
        log.info("删除文件：url={}", fileUrl);

        try {
            boolean success = fileStorageService.deleteFile(fileUrl);
            if (success) {
                return R.ok("文件删除成功");
            } else {
                return R.er(404, "文件不存在");
            }
        } catch (Exception e) {
            log.error("文件删除异常", e);
            return R.er(500, "文件删除失败");
        }
    }
}
