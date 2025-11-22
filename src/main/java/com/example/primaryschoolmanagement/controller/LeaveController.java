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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@Api(tags = "请假管理接口")
public class LeaveController {

    private static final Logger log = LoggerFactory.getLogger(LeaveController.class);

    @Resource
    private LeaveService leaveService;

    @PostMapping("")
    @ApiOperation("提交请假申请")
    public R submitLeave(@RequestBody LeaveDTO leaveDTO) {
        // 移除强转代码，Service内部安全获取用户信息
        try {
            Long leaveId = leaveService.submitLeave(leaveDTO);
            return R.ok(leaveId);
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("提交请假申请失败", e);
            return R.er(ResultCode.ERROR.getCode(), "提交失败");
        }
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 移除studentId参数，自动查询当前用户的记录
        try {
            IPage<LeaveVO> leavePage = leaveService.getMyLeaves(page, size);
            return R.ok(leavePage);
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation("撤回请假申请")
    public R cancelLeave(@PathVariable Long id) {
        // 移除userId参数，Service内部验证权限
        try {
            boolean success = leaveService.cancelLeave(id);
            return success ? R.ok() : R.er(ResultCode.ERROR.getCode(), "撤回失败");
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        }
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