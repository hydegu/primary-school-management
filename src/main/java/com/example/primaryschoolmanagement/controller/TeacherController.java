package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @RequestMapping
    public R movieList(){
        return this.teacherService.teacherList();
    }
}
