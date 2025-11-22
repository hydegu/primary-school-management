package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.ApprovalActionDTO;
import com.example.primaryschoolmanagement.service.ApprovalService;
import com.example.primaryschoolmanagement.vo.ApprovalVO;
import com.example.primaryschoolmanagement.vo.ApprovalNodeVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批管理Controller
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    /**
     * 提交审批（统一接口）
     */
    @PostMapping("/{id}/submit")
    public R submit(@PathVariable Long id, @RequestBody @Validated ApprovalActionDTO approvalActionDTO) {
        try {
            Long approverId = SecurityUtils.getCurrentUserId();
            if (approverId == null) {
                return R.er(401, "用户未登录");
            }

            if (approvalActionDTO.getApproved() == null) {
                return R.er(400, "请指定审批结果（approved字段）");
            }

            approvalActionDTO.setApprovalId(id);
            boolean result;
            String action;

            if (Boolean.TRUE.equals(approvalActionDTO.getApproved())) {
                result = approvalService.approve(approvalActionDTO, approverId);
                action = "通过";
            } else {
                result = approvalService.reject(approvalActionDTO, approverId);
                action = "拒绝";
            }

            return result ? R.ok("审批" + action + "成功") : R.er(ResultCode.ERROR.getCode(), "审批" + action + "失败");
        } catch (IllegalArgumentException e) {
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "审批失败: " + e.getMessage());
        }
    }

    /**
     * 待我审批
     */
    @GetMapping("/pending")
    public R getPendingApprovals(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long approverId = SecurityUtils.getCurrentUserId();
            if (approverId == null) {
                return R.er(401, "用户未登录");
            }

            IPage<ApprovalVO> pendingPage = approvalService.getPendingApprovals(approverId, page, size);
            return R.ok(pendingPage);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询待审批列表失败: " + e.getMessage());
        }
    }

    /**
     * 审批历史
     */
    @GetMapping("/history")
    public R getApprovalHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long applyUserId = SecurityUtils.getCurrentUserId();
            if (applyUserId == null) {
                return R.er(401, "用户未登录");
            }

            IPage<ApprovalVO> historyPage = approvalService.getApprovalHistory(applyUserId, page, size);
            return R.ok(historyPage);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询审批历史失败: " + e.getMessage());
        }
    }

    /**
     * 审批流程节点
     */
    @GetMapping("/{id}/nodes")
    public R getApprovalNodes(@PathVariable Long id) {
        try {
            List<ApprovalNodeVO> nodes = approvalService.getApprovalNodes(id);
            return R.ok(nodes);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询审批节点失败: " + e.getMessage());
        }
    }
}
