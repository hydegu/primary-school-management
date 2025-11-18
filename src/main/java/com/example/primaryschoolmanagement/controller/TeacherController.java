package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.TeacherQueryDTO;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @GetMapping(value="/teacherList")
    public R teacherList(Teacher teacher){
        return this.teacherService.teacherList();
    }
    // 组合查询接口：支持姓名、工号、职称的联合查询
    @PostMapping("/query")  // 改为POST请求
    public R queryTeachers(@RequestBody TeacherQueryDTO queryDTO) {  // 用@RequestBody接收JSON
        // 从DTO中获取参数
        return teacherService.queryByConditions(
                queryDTO.getTeacherName(),
                queryDTO.getTeacherNo(),
                queryDTO.getTitle()
        );
    }
    @GetMapping(value = "/teacherno")
    public R teacherNo(@RequestBody Teacher teacher){
        String teacher_no = teacher.getTeacherNo();
        return this.teacherService.teacherNo(teacher_no);
    }
    @GetMapping(value = "/teacherName")
    public R teacherName(@RequestBody Teacher teacher){
        String teacher_name = teacher.getTeacherName();
        return this.teacherService.teacherName(teacher_name);
    }
    @GetMapping(value = "/title")
    public R title(@RequestBody Teacher teacher){
        String title = teacher.getTitle();
        return this.teacherService.title(title);
    }
    @PostMapping(value="/addteacher")
    public R addteacher(@RequestBody Teacher teacher){
        return this.teacherService.addTeacher(teacher);
    }
    @PostMapping(value="/deleteteacher")
    public R deleteteacher(@RequestBody Teacher teacher){
        return this.teacherService.deleteTeacher(teacher);
    }
    @PostMapping(value="/updateteacher")
    public R updateteacher(@RequestBody Teacher teacher){
        return this.teacherService.updateTeacher(teacher);
    }
}
