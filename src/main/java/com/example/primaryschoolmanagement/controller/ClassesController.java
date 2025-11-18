package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @GetMapping(value="/class/list")
    public R classesList(){
        return this.classesService.classesList();
    }
    @PostMapping(value="/class")
    public R addclasses(@RequestBody Classes classes){
        return this.classesService.addclasses(classes);
    }
    @DeleteMapping(value="/class/{id}")
    public R deleteclasses(@PathVariable Integer id){
        return this.classesService.deleteclasses(id);
    }
    @PutMapping(value="/class/{id}")
    public R updateclasses(@RequestBody Classes classes){
        return  this.classesService.updateclasses(classes);
    }
}
