package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.CourseChange;
import com.example.primaryschoolmanagement.dto.CourseChangeDTO;
import com.example.primaryschoolmanagement.vo.CourseChangeVO;

/**
 * 调课业务逻辑接口
 */
public interface CourseChangeService extends IService<CourseChange> {

    /**
     * 提交调课申请
     * @param courseChangeDTO 调课申请数据
     * @param teacherId 当前教师ID
     * @return 调课记录ID
     */
    Long submitCourseChange(CourseChangeDTO courseChangeDTO, Long teacherId);

    /**
     * 查询调课详情
     * @param id 调课记录ID
     * @return 调课详情VO
     */
    CourseChangeVO getCourseChangeDetail(Long id);

    /**
     * 查询我的调课记录
     * @param teacherId 教师ID
     * @param page 页码
     * @param size 每页条数
     * @return 分页调课记录
     */
    IPage<CourseChangeVO> getMyCourseChanges(Long teacherId, int page, int size);
}