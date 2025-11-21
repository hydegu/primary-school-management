package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.ApprovalActionDTO;
import com.example.primaryschoolmanagement.service.ApprovalService;
import com.example.primaryschoolmanagement.vo.ApprovalVO;
import com.example.primaryschoolmanagement.vo.ApprovalNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval")
@Api(tags = "审批管理接口")
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    @PostMapping("/{id}/approve")
    @ApiOperation("审批通过")
    public R approve(@PathVariable Long id, @RequestBody @Validated ApprovalActionDTO approvalActionDTO) {
        try {
            Long approverId = SecurityUtils.getCurrentUserId();
            if (approverId == null) {
                return R.er(401, "用户未登录");
            }

            approvalActionDTO.setApprovalId(id);
            boolean result = approvalService.approve(approvalActionDTO, approverId);
            return result ? R.ok("审批通过成功") : R.er(ResultCode.ERROR.getCode(), "审批通过失败");
        } catch (IllegalArgumentException e) {
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "审批通过失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    @ApiOperation("审批拒绝")
    public R reject(@PathVariable Long id, @RequestBody @Validated ApprovalActionDTO approvalActionDTO) {
        try {
            Long approverId = SecurityUtils.getCurrentUserId();
            if (approverId == null) {
                return R.er(401, "用户未登录");
            }

            approvalActionDTO.setApprovalId(id);
            boolean result = approvalService.reject(approvalActionDTO, approverId);
            return result ? R.ok("审批拒绝成功") : R.er(ResultCode.ERROR.getCode(), "审批拒绝失败");
        } catch (IllegalArgumentException e) {
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "审批拒绝失败: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    @ApiOperation("待我审批")
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

    @GetMapping("/history")
    @ApiOperation("审批历史")
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

    @GetMapping("/{id}/nodes")
    @ApiOperation("审批流程节点")
    public R getApprovalNodes(@PathVariable Long id) {
        try {
            List<ApprovalNodeVO> nodes = approvalService.getApprovalNodes(id);
            return R.ok(nodes);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询审批节点失败: " + e.getMessage());
        }
    }
}
