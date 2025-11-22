package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 调班管理Controller
 */
@RestController
@RequestMapping("/api/class-transfer")
public class ClassTransferController {

    @Resource
    private ClassTransferService classTransferService;

    /**
     * 提交调班申请
     */
    @PostMapping("")
    public R submitClassTransfer(@RequestBody ClassTransferDTO classTransferDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return R.er(401, "用户未登录");
        }
        Long transferId = classTransferService.submitClassTransfer(classTransferDTO, userId);
        return R.ok(transferId);
    }

    /**
     * 查询调班详情
     */
    @GetMapping("/{id}")
    public R getClassTransferDetail(@PathVariable Long id) {
        ClassTransferVO classTransferVO = classTransferService.getClassTransferDetail(id);
        return R.ok(classTransferVO);
    }

    /**
     * 查询我的调班记录
     */
    @GetMapping("/my")
    public R getMyClassTransfers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long studentId = SecurityUtils.getCurrentUserId();
        if (studentId == null) {
            return R.er(401, "用户未登录");
        }
        IPage<ClassTransferVO> classTransferPage = classTransferService.getMyClassTransfers(studentId, page, size);
        return R.ok(classTransferPage);
    }
}
