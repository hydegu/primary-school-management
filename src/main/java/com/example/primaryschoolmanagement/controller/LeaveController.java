package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.LeaveDTO;
import com.example.primaryschoolmanagement.service.LeaveService;
import com.example.primaryschoolmanagement.vo.LeaveVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@Api(tags = "请假管理接口")
public class LeaveController {

    @Resource
    private LeaveService leaveService;

    @PostMapping("")
    @ApiOperation("提交请假申请")
    public R submitLeave(@RequestBody LeaveDTO leaveDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.valueOf(username);
        Long leaveId = leaveService.submitLeave(leaveDTO, userId);
        return R.ok(leaveId);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询请假详情")
    public R getLeaveDetail(@PathVariable Long id) {
        LeaveVO leaveVO = leaveService.getLeaveDetail(id);
        return R.ok(leaveVO);
    }

    @GetMapping("/my")
    @ApiOperation("查询我的请假记录")
    public R getMyLeaves(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<LeaveVO> leavePage = leaveService.getMyLeaves(studentId, page, size);
        return R.ok(leavePage);
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation("撤回请假申请")
    public R cancelLeave(
            @PathVariable Long id,
            @RequestParam Long userId) {
        boolean success = leaveService.cancelLeave(id, userId);
        return success ? R.ok() : R.er(ResultCode.ERROR.getCode(), "撤回失败");
    }

    @GetMapping("/pending")
    @ApiOperation("查询待审批请假列表")
    public R getPendingLeaves(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<LeaveVO> pendingPage = leaveService.getPendingLeaves(classId, page, size);
        return R.ok(pendingPage);
    }

}