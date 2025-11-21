package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    //班级列表
    @GetMapping(value="/class/list")
    public R classesList(){
        return this.classesService.classesList();
    }
    //添加班级
    @PostMapping(value="/class")
    public R addclasses(@RequestBody Classes classes, Teacher teacher){
        return this.classesService.addclasses(classes,teacher);
    }
    //删除班级
    @DeleteMapping(value="/class/{id}")
    public R deleteclasses(@PathVariable Integer id){
        return this.classesService.deleteclasses(id);
    }
    //更新班级
    @PutMapping(value="/class/{id}")
    public R updateclasses(@RequestBody Classes classes){
        return  this.classesService.updateclasses(classes);
    }
    //班级详情
    @GetMapping(value="/class/{id}")
    public R getclassById(@PathVariable Integer id){
        return this.classesService.getclassById(id);
    }
    //班级学生列表
    @GetMapping(value="/class/{id}/students")
    public List<Student> classStudent(@PathVariable Integer id){
        return (List<Student>) this.classesService.classStudent(id);
    }
    //分配班主任


}
