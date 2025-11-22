package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.subject.SubjectCreateRequest;
import com.example.primaryschoolmanagement.dto.subject.SubjectDTO;
import com.example.primaryschoolmanagement.dto.subject.SubjectUpdateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.entity.Subject;

import java.util.List;

/**
 * 科目服务接口
 */
public interface SubjectService extends IService<Subject> {

    /**
     * 查询科目列表
     * @return 科目列表
     */
    List<SubjectDTO> getSubjectList();

    /**
     * 根据ID获取科目详情
     * @param id 科目ID
     * @return 科目信息
     */
    SubjectDTO getSubjectById(Long id);

    /**
     * 添加科目
     * @param request 添加科目请求
     * @return 科目信息
     */
    SubjectDTO addSubject(SubjectCreateRequest request);

    /**
     * 更新科目信息
     * @param id 科目ID
     * @param request 更新科目请求
     * @return 科目信息
     */
    SubjectDTO updateSubject(Long id, SubjectUpdateRequest request);

    /**
     * 删除科目（软删除）
     * @param id 科目ID
     */
    void deleteSubject(Long id);

    /**
     * 获取科目的授课老师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    List<TeacherDTO> getSubjectTeachers(Long subjectId);

    /**
     * 为科目添加授课老师
     * @param subjectId 科目ID
     * @param teacherIds 教师ID列表
     */
    void addTeachersToSubject(Long subjectId, List<Long> teacherIds);

    /**
     * 移除科目的授课老师
     * @param subjectId 科目ID
     * @param teacherIds 教师ID列表
     */
    void removeTeachersFromSubject(Long subjectId, List<Long> teacherIds);
}
