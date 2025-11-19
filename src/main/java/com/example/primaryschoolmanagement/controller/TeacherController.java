package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    //教师详情
    @GetMapping(value="/teacher/{id}")
    public R getTeacherById(@PathVariable Integer id){
        return this.teacherService.getTeacherById(id);
    }

    //老师列表支持搜索
    @GetMapping("/teacher/list")
    public R queryByConditions(
            // 用@RequestParam接收查询参数，required = false表示可选（无参数时为null）
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String title) {

        // 调用服务层方法，传递搜索条件（null表示不筛选该字段）
        return teacherService.queryByConditions(teacherName, teacherNo, title);
    }
    //添加老师   信息会同步添加在user表
    @PostMapping(value="/teacher")
    public R addteacher(@RequestBody Teacher teacher, AppUser appuser){
        return this.teacherService.addTeacher(teacher,appuser);
    }
    //删除老师
    @DeleteMapping("/teacher/{id}")
    public R deleteTeacher(@PathVariable Integer id) { // 变量名与 {id} 一致时，可省略 "id" 参数
        return this.teacherService.deleteTeacher(id);
    }
   //更新老师
    @PutMapping(value="/teacher/{id}")
    public R updateteacher(@RequestBody Teacher teacher){
        return this.teacherService.updateTeacher(teacher);
    }
//    @GetMapping(value="/teacher/crouses")
//    public R getcrouseByteacherId(@PathVariable Integer id){
//        return  this.teacherService.getcrouseByteacherId(id);
//    }

}
