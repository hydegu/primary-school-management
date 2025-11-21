package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.CourseSwapDTO;
import com.example.primaryschoolmanagement.dto.CourseSwapConfirmDTO;
import com.example.primaryschoolmanagement.service.CourseSwapService;
import com.example.primaryschoolmanagement.vo.CourseSwapVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-swap")
@Api(tags = "换课管理接口")
public class CourseSwapController {

    @Resource
    private CourseSwapService courseSwapService;

    @PostMapping("")
    @ApiOperation("提交换课申请")
    public R submitCourseSwap(@RequestBody CourseSwapDTO courseSwapDTO) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        Long swapId = courseSwapService.submitCourseSwap(courseSwapDTO, teacherId);
        return R.ok(swapId);
    }

    @GetMapping("/my")
    @ApiOperation("查询我的换课记录")
    public R getMyCourseSwaps(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        IPage<CourseSwapVO> courseSwapPage = courseSwapService.getMyCourseSwaps(teacherId, page, size);
        return R.ok(courseSwapPage);
    }

    @PutMapping("/{id}/confirm")
    @ApiOperation("对方确认换课")
    public R confirmCourseSwap(
            @PathVariable Long id,
            @RequestBody CourseSwapConfirmDTO confirmDTO) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        if (teacherId == null) {
            return R.er(401, "用户未登录");
        }
        Boolean confirm = confirmDTO.getConfirm();
        boolean success = courseSwapService.confirmCourseSwap(id, teacherId, confirm);
        return success ? R.ok() : R.er(ResultCode.ERROR.getCode(), confirm ? "确认失败" : "拒绝失败");
    }
}
