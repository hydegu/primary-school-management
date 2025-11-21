package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.Leave;
import com.example.primaryschoolmanagement.dto.LeaveDTO;
import com.example.primaryschoolmanagement.vo.LeaveVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 请假业务逻辑接口
 */
public interface LeaveService extends IService<Leave> {

    /**
     * 提交请假申请
     * @param leaveDTO 请假申请数据
     * @param userId 当前用户ID
     * @return 请假记录ID
     */
    Long submitLeave(LeaveDTO leaveDTO, Long userId);

    /**
     * 查询请假详情
     * @param id 请假记录ID
     * @return 请假详情VO
     */
    LeaveVO getLeaveDetail(Long id);

    /**
     * 查询我的请假记录
     * @param studentId 学生ID
     * @param page 页码
     * @param size 每页条数
     * @return 分页请假记录
     */
    IPage<LeaveVO> getMyLeaves(Long studentId, int page, int size);

    /**
     * 撤回请假申请
     * @param id 请假记录ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean cancelLeave(Long id, Long userId);

    /**
     * 查询待审批请假列表
     * @param classId 班级ID（可选，用于筛选）
     * @param keyword 关键字（可选，模糊搜索学生姓名、请假原因）
     * @param page 页码
     * @param size 每页条数
     * @return 分页待审批列表
     */
    IPage<LeaveVO> getPendingLeaves(Long classId, String keyword, int page, int size);

}