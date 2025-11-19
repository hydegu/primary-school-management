package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.CourseSwap;
import com.example.primaryschoolmanagement.dto.CourseSwapDTO;
import com.example.primaryschoolmanagement.vo.CourseSwapVO;

/**
 * 换课业务逻辑接口
 */
public interface CourseSwapService extends IService<CourseSwap> {

    /**
     * 提交换课申请
     * @param courseSwapDTO 换课申请数据
     * @param teacherId 当前教师ID
     * @return 换课记录ID
     */
    Long submitCourseSwap(CourseSwapDTO courseSwapDTO, Long teacherId);

    /**
     * 查询我的换课记录
     * @param teacherId 教师ID
     * @param page 页码
     * @param size 每页条数
     * @return 分页换课记录
     */
    IPage<CourseSwapVO> getMyCourseSwaps(Long teacherId, int page, int size);

    /**
     * 对方确认换课
     * @param id 换课记录ID
     * @param teacherId 当前教师ID（目标教师）
     * @param confirm 是否确认
     * @return 是否成功
     */
    boolean confirmCourseSwap(Long id, Long teacherId, boolean confirm);
}