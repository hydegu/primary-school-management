package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.TeacherDTO;
import com.example.primaryschoolmanagement.dto.TeacherQueryDTO;
import com.example.primaryschoolmanagement.dto.subjectteacherDTO;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.entity.UserRole;
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
    public R queryByConditions(@RequestBody TeacherQueryDTO teacherQueryDTO) {

        // 调用服务层方法，传递搜索条件（null表示不筛选该字段）
        return teacherService.queryByConditions(teacherQueryDTO);
    }
    //添加老师   信息会同步添加在user表
    @PostMapping(value="/teacher")
    public R addteacher(@RequestBody Teacher teacher){
        return this.teacherService.addTeacher(teacher);
    }
    //删除老师
    @DeleteMapping("/teacher/{id}")
    public R deleteTeacher(
            @PathVariable ("id") Integer id
            ) { // 变量名与 {id} 一致时，可省略 "id" 参数
        return this.teacherService.deleteTeacher(id);
    }

@PutMapping(value="/teacher/{id}")
public R updateteacher(
        @PathVariable ("id") Integer id,
        @RequestBody Teacher teacher,
         AppUser appuser,
         UserRole userrole,
        subjectteacherDTO dto
        ){
    return this.teacherService.updateTeacher(id,teacher,appuser,userrole,dto);
}


    /**
     * 根据科目ID获取能教该科目的教师列表
     * 用于排课时根据选择的科目动态加载对应的教师
     * @param subjectId 科目ID
     * @return 教师列表
     */
    @GetMapping("/teacher/subject/{subjectId}")
    public R getTeachersBySubjectId(@PathVariable Long subjectId) {
        return teacherService.getTeachersBySubjectId(subjectId);
    }

}
