package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Grade;
import com.example.primaryschoolmanagement.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    //年级列表
    @GetMapping(value="/grade/list")
    public R gradeList(Grade grade){
        return this.gradeService.gradeList();
    }
}
