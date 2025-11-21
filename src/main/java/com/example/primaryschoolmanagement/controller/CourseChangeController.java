package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.CourseChangeDTO;
import com.example.primaryschoolmanagement.service.CourseChangeService;
import com.example.primaryschoolmanagement.vo.CourseChangeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-change")
@Api(tags = "调课管理接口")
public class CourseChangeController {

    @Resource
    private CourseChangeService courseChangeService;

    @PostMapping("")
    @ApiOperation("提交调课申请")
    public R submitCourseChange(@RequestBody CourseChangeDTO courseChangeDTO) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        Long changeId = courseChangeService.submitCourseChange(courseChangeDTO, teacherId);
        return R.ok(changeId);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询调课详情")
    public R getCourseChangeDetail(@PathVariable Long id) {
        CourseChangeVO courseChangeVO = courseChangeService.getCourseChangeDetail(id);
        return R.ok(courseChangeVO);
    }

    @GetMapping("/my")
    @ApiOperation("查询我的调课记录")
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
