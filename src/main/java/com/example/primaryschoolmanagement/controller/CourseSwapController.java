package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.CourseSwapDTO;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.CourseSwapService;
import com.example.primaryschoolmanagement.service.TeacherService;
import com.example.primaryschoolmanagement.service.UserService;
import com.example.primaryschoolmanagement.vo.CourseSwapVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/course-swap")
@Api(tags = "换课管理接口")
public class CourseSwapController {

    @Resource
    private CourseSwapService courseSwapService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private UserService userService;

    @PostMapping("")
    @ApiOperation("提交换课申请")
    public R submitCourseSwap(@RequestBody CourseSwapDTO courseSwapDTO) {
        try {
            // 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 根据用户名查询用户ID，再查询教师信息
            Teacher currentTeacher = getCurrentTeacher(username);
            if (currentTeacher == null) {
                return R.er(ResultCode.ERROR.getCode(), "当前用户不是教师或教师信息不存在");
            }

            Long swapId = courseSwapService.submitCourseSwap(courseSwapDTO, currentTeacher.getId().longValue());
            return R.ok(swapId);
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "系统异常，请稍后重试");
        }
    }

    @GetMapping("/my")
    @ApiOperation("查询我的换课记录")
    public R getMyCourseSwaps(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 根据用户名查询用户ID，再查询教师信息
            Teacher currentTeacher = getCurrentTeacher(username);
            if (currentTeacher == null) {
                return R.er(ResultCode.ERROR.getCode(), "当前用户不是教师或教师信息不存在");
            }

            IPage<CourseSwapVO> courseSwapPage = courseSwapService.getMyCourseSwaps(currentTeacher.getId().longValue(), page, size);
            return R.ok(courseSwapPage);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询失败");
        }
    }

    @PutMapping("/{id}/confirm")
    @ApiOperation("对方确认换课")
    public R confirmCourseSwap(
            @PathVariable Long id,
            @RequestParam Boolean confirm) {
        try {
            // 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 根据用户名查询用户ID，再查询教师信息
            Teacher currentTeacher = getCurrentTeacher(username);
            if (currentTeacher == null) {
                return R.er(ResultCode.ERROR.getCode(), "当前用户不是教师或教师信息不存在");
            }

            boolean success = courseSwapService.confirmCourseSwap(id, currentTeacher.getId().longValue(), confirm);
            return success ? R.ok() : R.er(ResultCode.ERROR.getCode(), confirm ? "确认失败" : "拒绝失败");
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "系统异常，请稍后重试");
        }
    }

    /**
     * 根据用户名获取当前教师信息
     * 通过 UserService 查询用户信息，再通过 TeacherService 查询教师信息
     */
    private Teacher getCurrentTeacher(String username) {
        try {
            // 1. 通过 UserService 根据用户名查询用户信息
            Optional<AppUser> userOptional = userService.findByIdentifier(username);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }

            AppUser user = userOptional.get();

            // 2. 检查用户类型是否为教师
            if (user.getUserType() != 2) { // 2 表示教师类型
                throw new IllegalArgumentException("当前用户不是教师");
            }

            // 3. 通过 TeacherService 根据用户ID查询教师信息
            Teacher teacher = teacherService.getTeacherByUserId(user.getId());
            if (teacher == null) {
                throw new IllegalArgumentException("教师信息不存在");
            }

            return teacher;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("无法获取当前教师信息，请检查登录状态");
        }
    }
}