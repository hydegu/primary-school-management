package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.ClassesService;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassTransferService classTransferService;

    //班级列表（支持模糊搜索：班级编号、班级名称、负责人姓名）
    @GetMapping(value="/class/list")
    public R classesList(
            @RequestParam(required = false) String classNo,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String headTeacherName){
        // 如果有任何搜索参数，则使用模糊搜索
        if (classNo != null || className != null || headTeacherName != null) {
            return this.classesService.searchClasses(classNo, className, headTeacherName);
        }
        return this.classesService.classesList();
    }
    //添加班级
    @PostMapping(value="/class")
    public R addclasses(@RequestBody Classes classes){
        return this.classesService.addclasses(classes);
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
    //班级学生列表 5.1
    @GetMapping(value="/class/{id}/students")
    public R classStudent(@PathVariable Integer id){
        if (id == null) {
            return R.er(400, "班级ID不能为空");
        }
        List<Student> students = this.classesService.classStudent(id);
        return R.ok(students);
    }

    //调班申请列表 5.3
    @GetMapping(value="/class/transfer")
    public R classTransferList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        IPage<ClassTransferVO> classTransferPage = this.classTransferService.getClassTransferList(page, size);
        return R.ok(classTransferPage);
    }

    //分配班主任


}
