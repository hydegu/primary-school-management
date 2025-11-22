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
     * @return 请假记录ID
     */
    Long submitLeave(LeaveDTO leaveDTO);

    /**
     * 查询请假详情
     * @param id 请假记录ID
     * @return 请假详情VO
     */
    LeaveVO getLeaveDetail(Long id);

    /**
     * 查询我的请假记录
     * @param page 页码
     * @param size 每页条数
     * @return 分页请假记录
     */
    IPage<LeaveVO> getMyLeaves(int page, int size);

    /**
     * 撤回请假申请
     * @param id 请假记录ID
     * @return 是否成功
     */
    boolean cancelLeave(Long id);

    /**
     * 查询待审批请假列表
     * @param classId 班级ID（班主任视角）
     * @param page 页码
     * @param size 每页条数
     * @return 分页待审批列表
     */
    IPage<LeaveVO> getPendingLeaves(Long classId, int page, int size);

}