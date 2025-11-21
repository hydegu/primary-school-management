package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.service.UserService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/class-transfer")
@Api(tags = "调班管理接口")
public class ClassTransferController {

    @Resource
    private ClassTransferService classTransferService;

    @Resource
    private UserService userService;

    @PostMapping("")
    @ApiOperation("提交调班申请")
    public R submitClassTransfer(@RequestBody ClassTransferDTO classTransferDTO) {
        try {
            // 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 根据用户名查询用户ID
            Long userId = getCurrentUserId(username);
            if (userId == null) {
                return R.er(ResultCode.ERROR.getCode(), "用户信息不存在");
            }

            Long transferId = classTransferService.submitClassTransfer(classTransferDTO, userId);
            return R.ok(transferId);
        } catch (IllegalArgumentException e) {
            return R.er(ResultCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "系统异常，请稍后重试");
        }
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
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 根据用户名查询用户ID
            Long userId = getCurrentUserId(username);
            if (userId == null) {
                return R.er(ResultCode.ERROR.getCode(), "用户信息不存在");
            }

            IPage<ClassTransferVO> classTransferPage = classTransferService.getMyClassTransfers(studentId, page, size);
            return R.ok(classTransferPage);
        } catch (Exception e) {
            return R.er(ResultCode.ERROR.getCode(), "查询失败");
        }
    }

    /**
     * 根据用户名获取当前用户ID
     * 通过 UserService 查询用户信息
     */
    private Long getCurrentUserId(String username) {
        try {
            // 通过 UserService 根据用户名查询用户信息
            Optional<AppUser> userOptional = userService.findByIdentifier(username);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }

            AppUser user = userOptional.get();
            return user.getId();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("无法获取当前用户信息，请检查登录状态");
        }
    }
}