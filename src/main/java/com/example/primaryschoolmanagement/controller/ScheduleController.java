package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.ScheduleInitDataDTO;
import com.example.primaryschoolmanagement.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 排课控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 获取排课初始化数据
     * 一次性返回所有排课所需的基础数据，避免前端多次请求
     *
     * 使用场景：
     * 1. 前端排课页面初始化时调用此接口
     * 2. 获取所有科目、教师、班级及其关联关系
     * 3. 前端缓存数据，当用户选择科目时，从缓存中筛选对应教师
     *
     * 优势：
     * 1. 减少HTTP请求次数，提升性能
     * 2. 避免网络延迟导致的加载等待
     * 3. 数据一致性更好
     * 4. 支持离线操作
     *
     * @return 包含科目、教师、班级及其关联关系的初始化数据
     */
    @GetMapping("/init-data")
    public R getInitData() {
        log.info("接收到获取排课初始化数据的请求");
        try {
            ScheduleInitDataDTO initData = scheduleService.getInitData();
            log.info("成功返回排课初始化数据");
            return R.ok(initData);
        } catch (Exception e) {
            log.error("获取排课初始化数据失败", e);
            return R.er(500, "获取排课初始化数据失败: " + e.getMessage());
        }
    }

}
