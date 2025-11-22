package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.CourseChangeDTO;
import com.example.primaryschoolmanagement.service.CourseChangeService;
import com.example.primaryschoolmanagement.vo.CourseChangeVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 调课管理Controller
 */
@RestController
@RequestMapping("/api/course-change")
public class CourseChangeController {

    @Resource
    private CourseChangeService courseChangeService;

    /**
     * 提交调课申请
     */
    @PostMapping("")
    public R submitCourseChange(@RequestBody CourseChangeDTO courseChangeDTO) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        Long changeId = courseChangeService.submitCourseChange(courseChangeDTO, teacherId);
        return R.ok(changeId);
    }

    /**
     * 查询调课详情
     */
    @GetMapping("/{id}")
    public R getCourseChangeDetail(@PathVariable Long id) {
        CourseChangeVO courseChangeVO = courseChangeService.getCourseChangeDetail(id);
        return R.ok(courseChangeVO);
    }

    /**
     * 查询我的调课记录
     */
    @GetMapping("/my")
    public R getMyCourseChanges(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        IPage<CourseChangeVO> courseChangePage = courseChangeService.getMyCourseChanges(teacherId, page, size);
        return R.ok(courseChangePage);
    }
}
