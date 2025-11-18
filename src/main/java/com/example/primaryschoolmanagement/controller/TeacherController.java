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
    @GetMapping(value="/teacher/{id}")
    public R teacherList(Teacher teacher){
        return this.teacherService.teacherList();
    }
    // 组合查询接口：支持姓名、工号、职称的联合查询
    @GetMapping("/teacher/list")  // 改为POST请求
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
    @PostMapping(value="/teacher")
    public R addteacher(@RequestBody Teacher teacher){
        return this.teacherService.addTeacher(teacher);
    }
    @DeleteMapping("/teacher/{id}")
    public R deleteTeacher(@PathVariable Integer id) { // 变量名与 {id} 一致时，可省略 "id" 参数
        return this.teacherService.deleteTeacher(id);
    }
    @PutMapping(value="/teacher/{id}")
    public R updateteacher(@RequestBody Teacher teacher){
        return this.teacherService.updateTeacher(teacher);
    }
}
