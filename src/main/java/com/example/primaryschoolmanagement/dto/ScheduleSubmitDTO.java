package com.example.primaryschoolmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleSubmitDTO {

    /**
     * 班级ID（必传）
     */
    @NotNull(message = "班级ID不能为空")
    private Integer classId;


    /**
     * 课表详情列表（按星期+节次组织）
     */
    @NotNull(message = "课表数据不能为空")
    private List<ScheduleDetailDTO> detailList;

    /**
     * 课表详情子DTO
     */
    @Data
    public static class ScheduleDetailDTO {
        /**
         * 星期（1=周一，2=周二...5=周五，必传）
         */
        @NotNull(message = "星期不能为空")
        private Integer weekDay;

        /**
         * 节次（1-5节，必传）
         */
        @NotNull(message = "节次不能为空")
        private Integer lessonNo;

        /**
         * 科目ID（必传）
         */
        @NotNull(message = "科目ID不能为空")
        private Long subjectId;

        /**
         * 教师ID（必传）
         */
        @NotNull(message = "教师ID不能为空")
        private Long teacherId;

        /**
         * 课表ID（新增时为空，修改时必传）
         */
        private Long scheduleId;
    }
}
