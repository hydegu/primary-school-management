package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.BusinessException;
import com.example.primaryschoolmanagement.common.exception.DuplicateException;
import com.example.primaryschoolmanagement.dao.SubjectDao;
import com.example.primaryschoolmanagement.dao.SubjectTeacherDao;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.dto.subject.SubjectCreateRequest;
import com.example.primaryschoolmanagement.dto.subject.SubjectDTO;
import com.example.primaryschoolmanagement.dto.subject.SubjectUpdateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.entity.SubjectTeacher;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 科目服务实现类
 */
@Slf4j
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectDao, Subject> implements SubjectService {

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectTeacherDao subjectTeacherDao;

    @Autowired
    private TeacherDao teacherDao;

    @Override
    public List<SubjectDTO> getSubjectList() {
        log.info("查询科目列表");

        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subject::getIsDeleted, false)
                .orderByAsc(Subject::getSortOrder)
                .orderByDesc(Subject::getCreatedAt);

        List<Subject> subjects = subjectDao.selectList(queryWrapper);

        return subjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectDTO getSubjectById(Long id) {
        log.info("查询科目详情：subjectId={}", id);

        Subject subject = subjectDao.selectById(id);
        if (subject == null || subject.getIsDeleted()) {
            throw new BusinessException("科目不存在或已删除");
        }

        return convertToDTO(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubjectDTO addSubject(SubjectCreateRequest request) {
        log.info("添加科目：subjectName={}, subjectCode={}", request.getSubjectName(), request.getSubjectCode());

        // 1. 检查科目编码是否已存在
        LambdaQueryWrapper<Subject> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(Subject::getSubjectCode, request.getSubjectCode())
                .eq(Subject::getIsDeleted, false);
        if (subjectDao.selectCount(codeQuery) > 0) {
            throw new DuplicateException("科目编码", request.getSubjectCode());
        }

        // 2. 检查科目名称是否已存在
        LambdaQueryWrapper<Subject> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(Subject::getSubjectName, request.getSubjectName())
                .eq(Subject::getIsDeleted, false);
        if (subjectDao.selectCount(nameQuery) > 0) {
            throw new DuplicateException("科目名称", request.getSubjectName());
        }

        // 3. 创建科目
        Subject subject = new Subject();
        BeanUtils.copyProperties(request, subject);
        subject.setStatus(1); // 默认启用
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
        subject.setIsDeleted(false);

        int result = subjectDao.insert(subject);
        if (result <= 0) {
            throw new BusinessException("添加科目失败");
        }

        log.info("科目添加成功：subjectId={}", subject.getId());
        return convertToDTO(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubjectDTO updateSubject(Long id, SubjectUpdateRequest request) {
        log.info("更新科目：subjectId={}", id);

        // 1. 查询科目是否存在
        Subject subject = subjectDao.selectById(id);
        if (subject == null || subject.getIsDeleted()) {
            throw new BusinessException("科目不存在或已删除");
        }

        // 2. 检查科目编码唯一性（如果修改）
        if (StringUtils.hasText(request.getSubjectCode()) && !request.getSubjectCode().equals(subject.getSubjectCode())) {
            LambdaQueryWrapper<Subject> codeQuery = new LambdaQueryWrapper<>();
            codeQuery.eq(Subject::getSubjectCode, request.getSubjectCode())
                    .eq(Subject::getIsDeleted, false)
                    .ne(Subject::getId, id);
            if (subjectDao.selectCount(codeQuery) > 0) {
                throw new DuplicateException("科目编码", request.getSubjectCode());
            }
        }

        // 3. 检查科目名称唯一性（如果修改）
        if (StringUtils.hasText(request.getSubjectName()) && !request.getSubjectName().equals(subject.getSubjectName())) {
            LambdaQueryWrapper<Subject> nameQuery = new LambdaQueryWrapper<>();
            nameQuery.eq(Subject::getSubjectName, request.getSubjectName())
                    .eq(Subject::getIsDeleted, false)
                    .ne(Subject::getId, id);
            if (subjectDao.selectCount(nameQuery) > 0) {
                throw new DuplicateException("科目名称", request.getSubjectName());
            }
        }

        // 4. 更新科目信息
        if (StringUtils.hasText(request.getSubjectName())) {
            subject.setSubjectName(request.getSubjectName());
        }
        if (StringUtils.hasText(request.getSubjectCode())) {
            subject.setSubjectCode(request.getSubjectCode());
        }
        if (request.getSortOrder() != null) {
            subject.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            subject.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getRemark())) {
            subject.setRemark(request.getRemark());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            subject.setAvatar(request.getAvatar());
        }
        subject.setUpdatedAt(LocalDateTime.now());

        int result = subjectDao.updateById(subject);
        if (result <= 0) {
            throw new BusinessException("更新科目失败");
        }

        log.info("科目更新成功：subjectId={}", id);
        return convertToDTO(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubject(Long id) {
        log.info("删除科目：subjectId={}", id);

        // 1. 查询科目是否存在
        Subject subject = subjectDao.selectById(id);
        if (subject == null || subject.getIsDeleted()) {
            throw new BusinessException("科目不存在或已删除");
        }

        // 2. 软删除科目
        LambdaUpdateWrapper<Subject> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Subject::getId, id)
                .set(Subject::getIsDeleted, true)
                .set(Subject::getUpdatedAt, LocalDateTime.now());

        int result = subjectDao.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("删除科目失败");
        }

        // 3. 删除科目-教师关联
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getSubjectId, id);
        subjectTeacherDao.delete(query);

        log.info("科目删除成功：subjectId={}", id);
    }

    @Override
    public List<TeacherDTO> getSubjectTeachers(Long subjectId) {
        log.info("查询科目的授课老师：subjectId={}", subjectId);

        // 1. 查询科目是否存在
        Subject subject = subjectDao.selectById(subjectId);
        if (subject == null || subject.getIsDeleted()) {
            throw new BusinessException("科目不存在或已删除");
        }

        // 2. 查询科目关联的教师ID列表
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getSubjectId, subjectId);
        List<SubjectTeacher> subjectTeachers = subjectTeacherDao.selectList(query);

        if (CollectionUtils.isEmpty(subjectTeachers)) {
            return new ArrayList<>();
        }

        // 3. 查询教师信息
        List<Long> teacherIds = subjectTeachers.stream()
                .map(SubjectTeacher::getTeacherId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Teacher> teacherQuery = new LambdaQueryWrapper<>();
        teacherQuery.in(Teacher::getId, teacherIds)
                .eq(Teacher::getIsDeleted, false);
        List<Teacher> teachers = teacherDao.selectList(teacherQuery);

        return teachers.stream()
                .map(this::convertTeacherToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTeachersToSubject(Long subjectId, List<Long> teacherIds) {
        log.info("为科目添加授课老师：subjectId={}, teacherIds={}", subjectId, teacherIds);

        if (CollectionUtils.isEmpty(teacherIds)) {
            return;
        }

        // 1. 检查科目是否存在
        Subject subject = subjectDao.selectById(subjectId);
        if (subject == null || subject.getIsDeleted()) {
            throw new BusinessException("科目不存在或已删除");
        }

        // 2. 批量添加教师关联
        for (Long teacherId : teacherIds) {
            // 检查教师是否存在
            Teacher teacher = teacherDao.selectById(teacherId);
            if (teacher == null || teacher.getIsDeleted()) {
                log.warn("教师不存在或已删除，跳过：teacherId={}", teacherId);
                continue;
            }

            // 检查是否已存在关联
            LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
            query.eq(SubjectTeacher::getSubjectId, subjectId)
                    .eq(SubjectTeacher::getTeacherId, teacherId);

            if (subjectTeacherDao.selectCount(query) == 0) {
                SubjectTeacher subjectTeacher = new SubjectTeacher();
                subjectTeacher.setSubjectId(subjectId);
                subjectTeacher.setTeacherId(teacherId);
                subjectTeacher.setCreatedAt(new java.util.Date());
                subjectTeacherDao.insert(subjectTeacher);
            }
        }

        log.info("授课老师添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTeachersFromSubject(Long subjectId, List<Long> teacherIds) {
        log.info("移除科目的授课老师：subjectId={}, teacherIds={}", subjectId, teacherIds);

        if (CollectionUtils.isEmpty(teacherIds)) {
            return;
        }

        // 删除科目-教师关联
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getSubjectId, subjectId)
                .in(SubjectTeacher::getTeacherId, teacherIds);

        subjectTeacherDao.delete(query);

        log.info("授课老师移除成功");
    }

    /**
     * 将Subject实体转换为DTO
     */
    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = SubjectDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())
                .sortOrder(subject.getSortOrder())
                .status(subject.getStatus())
                .remark(subject.getRemark())
                .avatar(subject.getAvatar())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();

        // 查询科目的授课教师ID列表和数量
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getSubjectId, subject.getId());
        List<SubjectTeacher> subjectTeachers = subjectTeacherDao.selectList(query);

        if (!CollectionUtils.isEmpty(subjectTeachers)) {
            List<Long> teacherIds = subjectTeachers.stream()
                    .map(SubjectTeacher::getTeacherId)
                    .collect(Collectors.toList());
            dto.setTeacherIds(teacherIds);
            dto.setTeacherCount(teacherIds.size());
        } else {
            dto.setTeacherCount(0);
        }

        return dto;
    }

    /**
     * 将Teacher实体转换为DTO（简化版）
     */
    private TeacherDTO convertTeacherToDTO(Teacher teacher) {
        return TeacherDTO.builder()
                .id(teacher.getId().longValue())
                .userId(teacher.getUserId() != null ? teacher.getUserId().longValue() : null)
                .teacherNo(teacher.getTeacherNo())
                .teacherName(teacher.getTeacherName())
                .gender(teacher.getGender())
                .birthDate(teacher.getBirthDate())
                .idCard(teacher.getIdCard())
                .phone(teacher.getPhone())
                .email(teacher.getEmail())
                .title(teacher.getTitle())
                .hireDate(teacher.getHireDate())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }
}
