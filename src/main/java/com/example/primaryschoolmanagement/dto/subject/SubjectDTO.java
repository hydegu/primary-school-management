package com.example.primaryschoolmanagement.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 科目信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    /**
     * 科目ID
     */
    private Long id;

    /**
     * 科目名称（语文/数学/英语/体育/微机/音乐/道法/科学）
     */
    private String subjectName;

    /**
     * 科目编码
     */
    private String subjectCode;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 封面图片URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 授课教师ID列表
     */
    private List<Long> teacherIds;

    /**
     * 授课教师数量
     */
    private Integer teacherCount;
}
