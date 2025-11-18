package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Grade;
import com.example.primaryschoolmanagement.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @GetMapping(value="/classesList")
    public R classesList(Classes classes){
        return this.classesService.classesList();
    }
    @PostMapping(value="/addclasses")
    public R addclasses(@RequestBody Classes classes){
        return this.classesService.addclasses(classes);
    }
    @PostMapping(value="/deleteclasses")
    public R deleteclasses(@RequestBody Classes classes){
        return this.classesService.deleteclasses(classes);
    }
    @PostMapping(value="/updateclasses")
    public R updateclasses(@RequestBody Classes classes){
        return  this.classesService.updateclasses(classes);
    }
}
