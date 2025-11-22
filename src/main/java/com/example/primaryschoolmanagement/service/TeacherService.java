package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.teacher.TeacherCreateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.dto.teacher.TeacherUpdateRequest;
import com.example.primaryschoolmanagement.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 查询教师列表（支持条件筛选）
     * @param teacherName 教师姓名（模糊查询）
     * @param teacherNo 教师工号
     * @param title 职称
     * @return 教师列表
     */
    List<TeacherDTO> queryTeacherList(String teacherName, String teacherNo, String title);

    /**
     * 根据ID获取教师详情
     * @param id 教师ID
     * @return 教师信息
     */
    TeacherDTO getTeacherDtoById(Long id);

    /**
     * 添加教师
     * @param request 添加教师请求
     * @return 教师信息
     */
    TeacherDTO addTeacher(TeacherCreateRequest request);

    /**
     * 删除教师（软删除）
     * @param id 教师ID
     */
    void deleteTeacher(Long id);

    /**
     * 更新教师信息
     * @param id 教师ID
     * @param request 更新教师请求
     * @return 教师信息
     */
    TeacherDTO updateTeacher(Long id, TeacherUpdateRequest request);

    /**
     * 根据科目ID获取能教该科目的教师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    List<TeacherDTO> getTeachersBySubjectId(Long subjectId);

    /**
     * 为教师添加科目
     * @param teacherId 教师ID
     * @param subjectIds 科目ID列表
     */
    void addSubjectsToTeacher(Long teacherId, List<Long> subjectIds);

    /**
     * 移除教师的科目
     * @param teacherId 教师ID
     * @param subjectIds 科目ID列表
     */
    void removeSubjectsFromTeacher(Long teacherId, List<Long> subjectIds);

    /**
     * 根据用户ID获取教师信息
     * @param userId 用户ID
     * @return 教师信息
     */
    Teacher getTeacherByUserId(Long userId);
}
