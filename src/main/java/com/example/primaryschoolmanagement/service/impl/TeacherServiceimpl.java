package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.BusinessException;
import com.example.primaryschoolmanagement.common.exception.DuplicateException;
import com.example.primaryschoolmanagement.dao.ClassesDao;
import com.example.primaryschoolmanagement.dao.SubjectTeacherDao;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dao.UserRoleDao;
import com.example.primaryschoolmanagement.dto.teacher.TeacherCreateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.dto.teacher.TeacherUpdateRequest;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.SubjectTeacher;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.entity.UserRole;
import com.example.primaryschoolmanagement.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师服务实现类
 */
@Slf4j
@Service
public class TeacherServiceimpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private SubjectTeacherDao subjectTeacherDao;

    @Autowired
    private ClassesDao classesDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<TeacherDTO> queryTeacherList(String teacherName, String teacherNo, String title) {
        log.info("查询教师列表：teacherName={}, teacherNo={}, title={}", teacherName, teacherNo, title);

        // 构建查询条件
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getIsDeleted, false);

        // 条件筛选
        if (StringUtils.hasText(teacherName)) {
            queryWrapper.like(Teacher::getTeacherName, teacherName.trim());
        }
        if (StringUtils.hasText(teacherNo)) {
            queryWrapper.like(Teacher::getTeacherNo, teacherNo.trim());
        }
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Teacher::getTitle, title.trim());
        }

        queryWrapper.orderByDesc(Teacher::getCreatedAt);

        List<Teacher> teachers = teacherDao.selectList(queryWrapper);

        return teachers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDTO getTeacherDtoById(Long id) {
        log.info("查询教师详情：teacherId={}", id);

        Teacher teacher = teacherDao.selectById(id);
        if (teacher == null || teacher.getIsDeleted()) {
            throw new BusinessException("教师不存在或已删除");
        }

        return convertToDTO(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherDTO addTeacher(TeacherCreateRequest request) {
        log.info("添加教师：teacherNo={}, teacherName={}", request.getTeacherNo(), request.getTeacherName());

        // 1. 检查教师编号是否已存在
        LambdaQueryWrapper<Teacher> query = new LambdaQueryWrapper<>();
        query.eq(Teacher::getTeacherNo, request.getTeacherNo())
                .eq(Teacher::getIsDeleted, false);
        if (teacherDao.selectCount(query) > 0) {
            throw new DuplicateException("教师编号", request.getTeacherNo());
        }

        // 2. 创建用户账号
        AppUser user = new AppUser();
        user.setUsername("teacher" + request.getTeacherNo());
        user.setPassword(passwordEncoder.encode("teacher123")); // 默认密码
        user.setRealName(request.getTeacherName());
        user.setUserType(2); // 教师类型
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);

        int userResult = userDao.insert(user);
        if (userResult <= 0) {
            throw new BusinessException("创建用户账号失败");
        }

        // 3. 创建教师记录
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(request, teacher);
        teacher.setUserId(user.getId());
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        teacher.setIsDeleted(false);

        int teacherResult = teacherDao.insert(teacher);
        if (teacherResult <= 0) {
            throw new BusinessException("创建教师记录失败");
        }

        // 4. 分配角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        // 根据职称分配角色：班主任为4，普通教师为3
        if ("班主任".equals(request.getTitle())) {
            userRole.setRoleId(4L);
        } else {
            userRole.setRoleId(3L);
        }
        userRole.setCreatedAt(LocalDateTime.now());

        int roleResult = userRoleDao.insert(userRole);
        if (roleResult <= 0) {
            throw new BusinessException("分配用户角色失败");
        }

        // 5. 添加科目关联
        if (!CollectionUtils.isEmpty(request.getSubjectIds())) {
            addSubjectsToTeacher(teacher.getId().longValue(), request.getSubjectIds());
        }

        log.info("教师添加成功：teacherId={}, userId={}", teacher.getId(), user.getId());
        return convertToDTO(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Long id) {
        log.info("删除教师：teacherId={}", id);

        // 1. 查询教师是否存在
        Teacher teacher = teacherDao.selectById(id);
        if (teacher == null || teacher.getIsDeleted()) {
            throw new BusinessException("教师不存在或已删除");
        }

        // 2. 软删除教师
        LambdaUpdateWrapper<Teacher> teacherUpdate = new LambdaUpdateWrapper<>();
        teacherUpdate.eq(Teacher::getId, id)
                .set(Teacher::getIsDeleted, true)
                .set(Teacher::getUpdatedAt, LocalDateTime.now());

        int teacherResult = teacherDao.update(null, teacherUpdate);
        if (teacherResult <= 0) {
            throw new BusinessException("删除教师失败");
        }

        // 3. 软删除关联的用户账号
        if (teacher.getUserId() != null) {
            LambdaUpdateWrapper<AppUser> userUpdate = new LambdaUpdateWrapper<>();
            userUpdate.eq(AppUser::getId, teacher.getUserId())
                    .set(AppUser::getIsDeleted, true)
                    .set(AppUser::getUpdatedAt, LocalDateTime.now());

            userDao.update(null, userUpdate);
        }

        // 4. 删除用户角色关联
        if (teacher.getUserId() != null) {
            LambdaQueryWrapper<UserRole> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(UserRole::getUserId, teacher.getUserId());
            userRoleDao.delete(roleQuery);
        }

        // 5. 删除科目关联
        LambdaQueryWrapper<SubjectTeacher> subjectQuery = new LambdaQueryWrapper<>();
        subjectQuery.eq(SubjectTeacher::getTeacherId, id);
        subjectTeacherDao.delete(subjectQuery);

        log.info("教师删除成功：teacherId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherDTO updateTeacher(Long id, TeacherUpdateRequest request) {
        log.info("更新教师信息：teacherId={}", id);

        // 1. 查询教师是否存在
        Teacher teacher = teacherDao.selectById(id);
        if (teacher == null || teacher.getIsDeleted()) {
            throw new BusinessException("教师不存在或已删除");
        }

        // 2. 检查教师编号唯一性（如果修改）
        if (StringUtils.hasText(request.getTeacherNo()) && !request.getTeacherNo().equals(teacher.getTeacherNo())) {
            LambdaQueryWrapper<Teacher> query = new LambdaQueryWrapper<>();
            query.eq(Teacher::getTeacherNo, request.getTeacherNo())
                    .eq(Teacher::getIsDeleted, false)
                    .ne(Teacher::getId, id);
            if (teacherDao.selectCount(query) > 0) {
                throw new DuplicateException("教师编号", request.getTeacherNo());
            }
        }

        // 3. 更新教师信息
        if (StringUtils.hasText(request.getTeacherNo())) {
            teacher.setTeacherNo(request.getTeacherNo());
        }
        if (StringUtils.hasText(request.getTeacherName())) {
            teacher.setTeacherName(request.getTeacherName());
        }
        if (request.getGender() != null) {
            teacher.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            teacher.setBirthDate(request.getBirthDate());
        }
        if (StringUtils.hasText(request.getIdCard())) {
            teacher.setIdCard(request.getIdCard());
        }
        if (StringUtils.hasText(request.getPhone())) {
            teacher.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            teacher.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getTitle())) {
            teacher.setTitle(request.getTitle());
        }
        if (request.getHireDate() != null) {
            teacher.setHireDate(request.getHireDate());
        }
        teacher.setUpdatedAt(LocalDateTime.now());

        int teacherResult = teacherDao.updateById(teacher);
        if (teacherResult <= 0) {
            throw new BusinessException("更新教师信息失败");
        }

        // 4. 同步更新用户信息
        if (teacher.getUserId() != null) {
            AppUser user = userDao.selectById(teacher.getUserId());
            if (user != null && !user.getIsDeleted()) {
                if (StringUtils.hasText(request.getTeacherName())) {
                    user.setRealName(request.getTeacherName());
                }
                if (StringUtils.hasText(request.getPhone())) {
                    user.setPhone(request.getPhone());
                }
                if (StringUtils.hasText(request.getEmail())) {
                    user.setEmail(request.getEmail());
                }
                if (request.getGender() != null) {
                    user.setGender(request.getGender());
                }
                user.setUpdatedAt(LocalDateTime.now());
                userDao.updateById(user);
            }

            // 5. 更新角色（根据职称变化）
            // 注意：只有职称明确为"班主任"时才自动设置班主任角色(4)
            // 其他职称变化不会自动修改角色，需要通过角色管理接口单独设置
            if (StringUtils.hasText(request.getTitle()) && "班主任".equals(request.getTitle())) {
                LambdaQueryWrapper<UserRole> roleQuery = new LambdaQueryWrapper<>();
                roleQuery.eq(UserRole::getUserId, teacher.getUserId());
                UserRole userRole = userRoleDao.selectOne(roleQuery);

                if (userRole != null && !userRole.getRoleId().equals(4L)) {
                    userRole.setRoleId(4L); // 班主任角色
                    userRoleDao.updateById(userRole);
                    log.info("已将教师 {} 的角色更新为班主任角色", id);
                }
            }
        }

        // 6. 更新科目关联
        if (request.getSubjectIds() != null) {
            // 先删除所有科目关联
            LambdaQueryWrapper<SubjectTeacher> deleteQuery = new LambdaQueryWrapper<>();
            deleteQuery.eq(SubjectTeacher::getTeacherId, id);
            subjectTeacherDao.delete(deleteQuery);

            // 重新添加科目关联
            if (!CollectionUtils.isEmpty(request.getSubjectIds())) {
                addSubjectsToTeacher(id, request.getSubjectIds());
            }
        }

        // 7. 更新班主任与班级的关联
        if (request.getClassIds() != null) {
            // 先清除该教师在所有班级中的班主任关联
            LambdaUpdateWrapper<Classes> clearWrapper = new LambdaUpdateWrapper<>();
            clearWrapper.eq(Classes::getHeadTeacherId, id.intValue())
                    .set(Classes::getHeadTeacherId, null)
                    .set(Classes::getUpdatedAt, LocalDateTime.now());
            classesDao.update(null, clearWrapper);
            log.info("已清除教师 {} 的所有班主任关联", id);

            // 如果提供了新的班级列表，则更新班主任关联
            if (!CollectionUtils.isEmpty(request.getClassIds())) {
                // 验证班级是否存在且未被删除
                for (Long classId : request.getClassIds()) {
                    Classes clazz = classesDao.selectById(classId.intValue());
                    if (clazz == null || clazz.getIsDeleted() == 1) {
                        throw new BusinessException("班级ID " + classId + " 不存在或已删除");
                    }

                    // 检查班级是否已有其他班主任
                    if (clazz.getHeadTeacherId() != null && !clazz.getHeadTeacherId().equals(id.intValue())) {
                        throw new BusinessException("班级 " + clazz.getClassName() + " 已有班主任，请先解除原班主任关联");
                    }
                }

                // 更新新班级的班主任
                LambdaUpdateWrapper<Classes> updateWrapper = new LambdaUpdateWrapper<>();
                List<Integer> classIdInts = request.getClassIds().stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                updateWrapper.in(Classes::getId, classIdInts)
                        .set(Classes::getHeadTeacherId, id.intValue())
                        .set(Classes::getUpdatedAt, LocalDateTime.now());
                int updatedCount = classesDao.update(null, updateWrapper);
                log.info("为教师 {} 分配了 {} 个班级的班主任职责", id, updatedCount);
            }
        }

        // 8. 业务逻辑验证和警告
        // 检查职称与班级分配的一致性
        if (StringUtils.hasText(request.getTitle()) && "班主任".equals(request.getTitle())) {
            // 检查是否分配了班级
            LambdaQueryWrapper<Classes> classQuery = new LambdaQueryWrapper<>();
            classQuery.eq(Classes::getHeadTeacherId, id.intValue())
                    .eq(Classes::getIsDeleted, 0);
            long classCount = classesDao.selectCount(classQuery);

            if (classCount == 0) {
                log.warn("教师 {} 的职称为班主任，但未分配任何班级", id);
            }
        }

        // 如果分配了班级但职称不是班主任，记录警告
        if (request.getClassIds() != null && !CollectionUtils.isEmpty(request.getClassIds())) {
            if (!StringUtils.hasText(request.getTitle()) || !"班主任".equals(request.getTitle())) {
                // 检查当前教师的职称
                if (!StringUtils.hasText(teacher.getTitle()) || !"班主任".equals(teacher.getTitle())) {
                    log.warn("教师 {} 分配了班级但职称不是班主任，当前职称：{}", id, teacher.getTitle());
                }
            }
        }

        log.info("教师更新成功：teacherId={}", id);
        return convertToDTO(teacher);
    }

    @Override
    public List<TeacherDTO> getTeachersBySubjectId(Long subjectId) {
        log.info("根据科目查询教师列表：subjectId={}", subjectId);

        if (subjectId == null) {
            throw new BusinessException("科目ID不能为空");
        }

        // 查询科目关联的教师ID列表
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getSubjectId, subjectId);
        List<SubjectTeacher> subjectTeachers = subjectTeacherDao.selectList(query);

        if (CollectionUtils.isEmpty(subjectTeachers)) {
            return new ArrayList<>();
        }

        // 查询教师信息
        List<Long> teacherIds = subjectTeachers.stream()
                .map(SubjectTeacher::getTeacherId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Teacher> teacherQuery = new LambdaQueryWrapper<>();
        teacherQuery.in(Teacher::getId, teacherIds)
                .eq(Teacher::getIsDeleted, false);
        List<Teacher> teachers = teacherDao.selectList(teacherQuery);

        return teachers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSubjectsToTeacher(Long teacherId, List<Long> subjectIds) {
        log.info("为教师添加科目：teacherId={}, subjectIds={}", teacherId, subjectIds);

        if (CollectionUtils.isEmpty(subjectIds)) {
            return;
        }

        // 检查教师是否存在
        Teacher teacher = teacherDao.selectById(teacherId);
        if (teacher == null || teacher.getIsDeleted()) {
            throw new BusinessException("教师不存在或已删除");
        }

        // 批量添加科目关联
        for (Long subjectId : subjectIds) {
            // 检查是否已存在关联
            LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
            query.eq(SubjectTeacher::getTeacherId, teacherId)
                    .eq(SubjectTeacher::getSubjectId, subjectId);

            if (subjectTeacherDao.selectCount(query) == 0) {
                SubjectTeacher subjectTeacher = new SubjectTeacher();
                subjectTeacher.setTeacherId(teacherId);
                subjectTeacher.setSubjectId(subjectId);
                subjectTeacherDao.insert(subjectTeacher);
            }
        }

        log.info("科目添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSubjectsFromTeacher(Long teacherId, List<Long> subjectIds) {
        log.info("移除教师的科目：teacherId={}, subjectIds={}", teacherId, subjectIds);

        if (CollectionUtils.isEmpty(subjectIds)) {
            return;
        }

        // 删除科目关联
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getTeacherId, teacherId)
                .in(SubjectTeacher::getSubjectId, subjectIds);

        subjectTeacherDao.delete(query);

        log.info("科目移除成功");
    }

    @Override
    public Teacher getTeacherByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapper<Teacher> query = new LambdaQueryWrapper<>();
        query.eq(Teacher::getUserId, userId)
                .eq(Teacher::getIsDeleted, false);

        return teacherDao.selectOne(query);
    }

    /**
     * 将Teacher实体转换为DTO
     */
    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = TeacherDTO.builder()
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

        // 查询教师的科目ID列表
        LambdaQueryWrapper<SubjectTeacher> query = new LambdaQueryWrapper<>();
        query.eq(SubjectTeacher::getTeacherId, teacher.getId());
        List<SubjectTeacher> subjectTeachers = subjectTeacherDao.selectList(query);

        if (!CollectionUtils.isEmpty(subjectTeachers)) {
            List<Long> subjectIds = subjectTeachers.stream()
                    .map(SubjectTeacher::getSubjectId)
                    .collect(Collectors.toList());
            dto.setSubjectIds(subjectIds);
        }

        return dto;
    }
}
