package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.ClassTransfer;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;

/**
 * 调班业务逻辑接口
 */
public interface ClassTransferService extends IService<ClassTransfer> {

    /**
     * 提交调班申请
     * @param classTransferDTO 调班申请数据
     * @param userId 当前用户ID
     * @return 调班记录ID
     */
    Long submitClassTransfer(ClassTransferDTO classTransferDTO, Long userId);

    /**
     * 查询调班详情
     * @param id 调班记录ID
     * @return 调班详情VO
     */
    ClassTransferVO getClassTransferDetail(Long id);

    /**
     * 查询我的调班记录
     * @param studentId 学生ID
     * @param page 页码
     * @param size 每页条数
     * @return 分页调班记录
     */
    IPage<ClassTransferVO> getMyClassTransfers(Long studentId, int page, int size);
}