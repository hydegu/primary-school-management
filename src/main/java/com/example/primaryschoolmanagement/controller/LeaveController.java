package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.LeaveDTO;
import com.example.primaryschoolmanagement.service.LeaveService;
import com.example.primaryschoolmanagement.vo.LeaveVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 请假管理Controller
 */
@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private static final Logger log = LoggerFactory.getLogger(LeaveController.class);

    @Resource
    private LeaveService leaveService;

    /**
     * 提交请假申请
     */
    @PostMapping("")
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

    /**
     * 查询请假详情
     */
    @GetMapping("/{id}")
    public R getLeaveDetail(@PathVariable Long id) {
        LeaveVO leaveVO = leaveService.getLeaveDetail(id);
        return R.ok(leaveVO);
    }

    /**
     * 查询我的请假记录
     */
    @GetMapping("/my")
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

    /**
     * 撤回请假申请
     */
    @PutMapping("/{id}/cancel")
    public R cancelLeave(@PathVariable Long id) {
        // 移除userId参数，Service内部验证权限
        try {
            boolean success = leaveService.cancelLeave(id);
            return success ? R.ok() : R.er(ResultCode.ERROR.getCode(), "撤回失败");
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        }
    }

    /**
     * 查询待审批请假列表
     */
    @GetMapping("/pending")
    public R getPendingLeaves(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<LeaveVO> pendingPage = leaveService.getPendingLeaves(classId, page, size);
        return R.ok(pendingPage);
    }
}