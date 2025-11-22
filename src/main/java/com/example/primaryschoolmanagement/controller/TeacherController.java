package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.teacher.TeacherCreateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.dto.teacher.TeacherUpdateRequest;
import com.example.primaryschoolmanagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 教师列表查询（支持条件筛选）
     */
    @GetMapping("/teacher/list")
    public R getTeacherList(
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String title) {

        log.info("查询教师列表：teacherName={}, teacherNo={}, title={}", teacherName, teacherNo, title);
        List<TeacherDTO> teachers = teacherService.queryTeacherList(teacherName, teacherNo, title);
        return R.ok(teachers);
    }

    /**
     * 教师详情
     */
    @GetMapping("/teacher/{id}")
    public R getTeacherById(@PathVariable Long id) {
        log.info("查询教师详情：id={}", id);
        TeacherDTO teacher = teacherService.getTeacherDtoById(id);
        return R.ok(teacher);
    }

    /**
     * 添加教师
     */
    @PostMapping("/teacher")
    public R addTeacher(@Valid @RequestBody TeacherCreateRequest request) {
        log.info("添加教师：teacherNo={}, teacherName={}", request.getTeacherNo(), request.getTeacherName());
        TeacherDTO teacher = teacherService.addTeacher(request);
        return R.ok(teacher);
    }

    /**
     * 修改教师
     */
    @PutMapping("/teacher/{id}")
    public R updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherUpdateRequest request) {
        log.info("修改教师：id={}", id);
        TeacherDTO teacher = teacherService.updateTeacher(id, request);
        return R.ok(teacher);
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/teacher/{id}")
    public R deleteTeacher(@PathVariable Long id) {
        log.info("删除教师：id={}", id);
        teacherService.deleteTeacher(id);
        return R.ok("删除成功");
    }

    /**
     * 根据科目ID获取能教该科目的教师列表
     */
    @GetMapping("/teacher/subject/{subjectId}")
    public R getTeachersBySubjectId(@PathVariable Long subjectId) {
        log.info("根据科目查询教师：subjectId={}", subjectId);
        List<TeacherDTO> teachers = teacherService.getTeachersBySubjectId(subjectId);
        return R.ok(teachers);
    }

    /**
     * 为教师添加科目
     */
    @PostMapping("/teacher/{teacherId}/subjects")
    public R addSubjectsToTeacher(@PathVariable Long teacherId, @RequestBody List<Long> subjectIds) {
        log.info("为教师添加科目：teacherId={}, subjectIds={}", teacherId, subjectIds);
        teacherService.addSubjectsToTeacher(teacherId, subjectIds);
        return R.ok("科目添加成功");
    }

    /**
     * 移除教师的科目
     */
    @DeleteMapping("/teacher/{teacherId}/subjects")
    public R removeSubjectsFromTeacher(@PathVariable Long teacherId, @RequestBody List<Long> subjectIds) {
        log.info("移除教师的科目：teacherId={}, subjectIds={}", teacherId, subjectIds);
        teacherService.removeSubjectsFromTeacher(teacherId, subjectIds);
        return R.ok("科目移除成功");
    }
}
