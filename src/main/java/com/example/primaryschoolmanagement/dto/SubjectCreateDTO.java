package com.example.primaryschoolmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SubjectCreateDTO {
    // 科目名称必填，且不能是空白字符串（配合trim()）
    @NotBlank(message = "科目名称不能为空")
    private String subjectName;

    // 科目编码必填
    @NotBlank(message = "科目编码不能为空")
    private String subjectCode;

    // 状态可选（默认1），若传递则必须是合法值（可额外加@Min(0)@Max(1)）
    private Integer status;

    // 删除标记可选（默认false），不允许前端传递true（避免创建时直接标记删除）
    @Null(message = "删除标记不允许手动设置")
    private Boolean isDeleted;

    // 封面文件可选
    private MultipartFile coverFile;

    // 上传封面时必填，否则可选（根据业务调整）
    @NotNull(message = "科目ID不能为空")
    private Long id;
}
