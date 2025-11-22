package com.example.primaryschoolmanagement.dto.subject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改科目请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectUpdateRequest {

    /**
     * 科目名称
     */
    @Size(max = 50, message = "科目名称长度不能超过50个字符")
    private String subjectName;

    /**
     * 科目编码
     */
    @Size(max = 50, message = "科目编码长度不能超过50个字符")
    private String subjectCode;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-停用 1-启用
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 封面图片URL
     */
    @Size(max = 255, message = "封面URL长度不能超过255个字符")
    private String avatar;
}
