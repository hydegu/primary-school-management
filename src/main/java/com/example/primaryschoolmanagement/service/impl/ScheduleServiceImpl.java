package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.primaryschoolmanagement.dao.ClassesDao;
import com.example.primaryschoolmanagement.dao.ScheduleDao;
import com.example.primaryschoolmanagement.dao.SubjectDao;
import com.example.primaryschoolmanagement.dao.SubjectTeacherDao;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.dto.ScheduleInitDataDTO;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.entity.SubjectTeacher;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.ScheduleService;
import com.example.primaryschoolmanagement.vo.ScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排课服务实现类
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final SubjectDao subjectDao;
    private final TeacherDao teacherDao;
    private final ClassesDao classesDao;
    private final SubjectTeacherDao subjectTeacherDao;
    private final ScheduleDao scheduleDao;

    public ScheduleServiceImpl(SubjectDao subjectDao,
                               TeacherDao teacherDao,
                               ClassesDao classesDao,
                               SubjectTeacherDao subjectTeacherDao,
                               ScheduleDao scheduleDao) {
        this.subjectDao = subjectDao;
        this.teacherDao = teacherDao;
        this.classesDao = classesDao;
        this.subjectTeacherDao = subjectTeacherDao;
        this.scheduleDao = scheduleDao;
    }

    /**
     * 获取排课初始化数据
     *
     * @return 排课初始化数据
     */
    @Override
    public ScheduleInitDataDTO getInitData() {
        log.info("开始获取排课初始化数据");

        ScheduleInitDataDTO initData = new ScheduleInitDataDTO();

        // 1. 查询所有启用的科目
        LambdaQueryWrapper<Subject> subjectWrapper = new LambdaQueryWrapper<>();
        subjectWrapper.eq(Subject::getStatus, 1)
                      .eq(Subject::getIsDeleted, 0)
                      .orderByAsc(Subject::getSortOrder);
        List<Subject> subjects = subjectDao.selectList(subjectWrapper);
        initData.setSubjects(subjects);
        log.info("查询到 {} 个科目", subjects.size());

        // 2. 查询所有未删除的教师
        LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(Teacher::getIsDeleted, 0);
        List<Teacher> teachers = teacherDao.selectList(teacherWrapper);
        initData.setTeachers(teachers);
        log.info("查询到 {} 个教师", teachers.size());

        // 3. 查询所有启用的班级
        LambdaQueryWrapper<Classes> classWrapper = new LambdaQueryWrapper<>();
        classWrapper.eq(Classes::getStatus, 1)
                    .eq(Classes::getIsDeleted, 0);
        List<Classes> classes = classesDao.selectList(classWrapper);
        initData.setClasses(classes);
        log.info("查询到 {} 个班级", classes.size());

        // 4. 构建科目-教师映射关系
        List<SubjectTeacher> subjectTeachers = subjectTeacherDao.selectList(null);

        // 使用Stream API进行分组
        Map<Long, List<Long>> subjectTeacherMap = subjectTeachers.stream()
            .collect(Collectors.groupingBy(
                SubjectTeacher::getSubjectId,
                Collectors.mapping(SubjectTeacher::getTeacherId, Collectors.toList())
            ));

        initData.setSubjectTeacherMap(subjectTeacherMap);
        log.info("构建了 {} 个科目的教师映射关系", subjectTeacherMap.size());

        log.info("排课初始化数据获取完成");
        return initData;
    }

    /**
     * 根据班级ID查询课表
     * @param classId 班级ID
     * @return 该班级的所有排课信息
     */
    @Override
    public List<ScheduleVO> getScheduleByClassId(Integer classId) {
        log.info("查询班级课表，classId: {}", classId);
        List<ScheduleVO> scheduleList = scheduleDao.scheduleListByClassId(classId);
        log.info("查询到 {} 条排课记录", scheduleList != null ? scheduleList.size() : 0);
        return scheduleList != null ? scheduleList : List.of();
    }
}
