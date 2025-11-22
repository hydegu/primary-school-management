package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.subject.SubjectCreateRequest;
import com.example.primaryschoolmanagement.dto.subject.SubjectDTO;
import com.example.primaryschoolmanagement.dto.subject.SubjectUpdateRequest;
import com.example.primaryschoolmanagement.dto.teacher.TeacherDTO;
import com.example.primaryschoolmanagement.service.SubjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科目管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 科目列表查询
     */
    @GetMapping("/subject/list")
    public R getSubjectList() {
        log.info("查询科目列表");
        List<SubjectDTO> subjects = subjectService.getSubjectList();
        return R.ok(subjects);
    }

    /**
     * 科目详情
     */
    @GetMapping("/subject/{id}")
    public R getSubjectById(@PathVariable Long id) {
        log.info("查询科目详情：id={}", id);
        SubjectDTO subject = subjectService.getSubjectById(id);
        return R.ok(subject);
    }

    /**
     * 添加科目
     */
    @PostMapping("/subject")
    public R addSubject(@Valid @RequestBody SubjectCreateRequest request) {
        log.info("添加科目：subjectName={}, subjectCode={}", request.getSubjectName(), request.getSubjectCode());
        SubjectDTO subject = subjectService.addSubject(request);
        return R.ok(subject);
    }

    /**
     * 修改科目
     */
    @PutMapping("/subject/{id}")
    public R updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectUpdateRequest request) {
        log.info("修改科目：id={}", id);
        SubjectDTO subject = subjectService.updateSubject(id, request);
        return R.ok(subject);
    }

    /**
     * 删除科目
     */
    @DeleteMapping("/subject/{id}")
    public R deleteSubject(@PathVariable Long id) {
        log.info("删除科目：id={}", id);
        subjectService.deleteSubject(id);
        return R.ok("删除成功");
    }

    /**
     * 查询科目的授课老师列表
     */
    @GetMapping("/subject/{id}/teachers")
    public R getSubjectTeachers(@PathVariable Long id) {
        log.info("查询科目的授课老师：subjectId={}", id);
        List<TeacherDTO> teachers = subjectService.getSubjectTeachers(id);
        return R.ok(teachers);
    }

    /**
     * 为科目添加授课老师
     */
    @PostMapping("/subject/{id}/teachers")
    public R addTeachersToSubject(@PathVariable Long id, @RequestBody List<Long> teacherIds) {
        log.info("为科目添加授课老师：subjectId={}, teacherIds={}", id, teacherIds);
        subjectService.addTeachersToSubject(id, teacherIds);
        return R.ok("授课老师添加成功");
    }

    /**
     * 移除科目的授课老师
     */
    @DeleteMapping("/subject/{id}/teachers")
    public R removeTeachersFromSubject(@PathVariable Long id, @RequestBody List<Long> teacherIds) {
        log.info("移除科目的授课老师：subjectId={}, teacherIds={}", id, teacherIds);
        subjectService.removeTeachersFromSubject(id, teacherIds);
        return R.ok("授课老师移除成功");
    }
}
