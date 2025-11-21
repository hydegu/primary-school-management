package com.example.primaryschoolmanagement.service;

import com.example.primaryschoolmanagement.dto.ScheduleInitDataDTO;
import com.example.primaryschoolmanagement.vo.ScheduleVO;

import java.util.List;

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

    /**
     * 根据班级ID查询课表
     * @param classId 班级ID
     * @return 该班级的所有排课信息（包含科目名、教师名、班级名、课程名）
     */
    List<ScheduleVO> getScheduleByClassId(Integer classId);
}
