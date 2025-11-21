package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/class-transfer")
@Api(tags = "调班管理接口")
public class ClassTransferController {

    @Resource
    private ClassTransferService classTransferService;

    @PostMapping("")
    @ApiOperation("提交调班申请")
    public R submitClassTransfer(@RequestBody ClassTransferDTO classTransferDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return R.er(401, "用户未登录");
        }
        Long transferId = classTransferService.submitClassTransfer(classTransferDTO, userId);
        return R.ok(transferId);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询调班详情")
    public R getClassTransferDetail(@PathVariable Long id) {
        ClassTransferVO classTransferVO = classTransferService.getClassTransferDetail(id);
        return R.ok(classTransferVO);
    }

    @GetMapping("/my")
    @ApiOperation("查询我的调班记录")
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
