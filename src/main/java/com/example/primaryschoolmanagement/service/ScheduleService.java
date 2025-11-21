package com.example.primaryschoolmanagement.service;

import com.example.primaryschoolmanagement.dto.ScheduleInitDataDTO;

/**
 * 排课服务接口
 */
public interface ScheduleService {

    /**
     * 获取排课初始化数据
     * 一次性返回所有排课所需的基础数据，包括：
     * - 所有科目
     * - 所有教师
     * - 所有班级
     * - 科目-教师映射关系
     *
     * @return 排课初始化数据
     */
    ScheduleInitDataDTO getInitData();
}
