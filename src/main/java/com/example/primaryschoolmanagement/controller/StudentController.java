package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.StudentDao;
import com.example.primaryschoolmanagement.dto.StudentDto;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.service.StudentService;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
    @Resource
    private UserService userService;
    @Autowired
    private StudentDao studentDao;


    @PostMapping(value = "")

    public R createStudent(@RequestBody StudentDto dto) {
       if(dto == null || !StringUtils.hasText(dto.getStudentNo()) || !StringUtils.hasText(dto.getStudentName())){
           return R.er(400,"学号和姓名不能为空");
       }
           int row = studentService.createStudent(dto);
           return row > 0 ? R.ok("新增成功"):R.er();
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
        if(dto == null){
            return R.er(400,"更改信息不能为空");
        }
        if(!studentService.updateStudent(dto)){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"更新失败");
        }
        return R.ok();
    }

    @DeleteMapping(value = "/{id}")
    public R delete(@PathVariable Integer id){

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
