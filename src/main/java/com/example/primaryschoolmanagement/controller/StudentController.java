package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")

public class StudentController {

    @Resource
    private StudentService studentService;

    @PostMapping(value = "")
    public R createStudent(@RequestBody Student dto) {
       if(dto == null || !StringUtils.hasText(dto.getStudentNo()) || !StringUtils.hasText(dto.getStudentName())){
           return R.er(400,"学号和姓名不能为空");
       }
       try {
           int row = studentService.createStudent(dto);
           return row > 0 ? R.ok("新增成功"):R.er();
       }catch (Exception e){
           return R.er(500,"新增异常"+e.getMessage());
       }

    }
    @GetMapping(value = "/list")
    public R list(@RequestParam(required = false) Map<String,Object> map){
        try {
            List<Student> studentList = studentService.list(map);
            return R.ok(studentList);
        }catch (Exception e){
            return R.er(500,"查询异常"+e.getMessage());
        }
    }
    @PutMapping(value = "/{id}")
    public R updateStudent(@RequestBody Student dto){
        if(dto == null || !StringUtils.hasText(dto.getStudentNo()) || !StringUtils.hasText(dto.getStudentName())){
            return R.er(400,"学号不能为空");
        }
        try {
            int row = studentService.updateStudent(dto);
            return row > 0 ? R.ok("更新成功"):R.er();
        }catch (Exception e){
            return R.er(500,"更新异常"+e.getMessage());
        }

    }
    @DeleteMapping(value = "/{id}")
    public R delete(@RequestBody Integer id){

        if(id == null ){
            return R.er(400,"学号不能为空");
        }
        try {
            int row = studentService.delete(id);
            return row > 0 ? R.ok("删除成功"):R.er();
        }catch (Exception e){
            return R.er(500,"删除异常"+ e.getMessage());
        }

    }
}
