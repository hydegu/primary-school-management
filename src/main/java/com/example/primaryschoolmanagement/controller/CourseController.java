package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.SubjectTeacherRelationDTO;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.service.CourseService;
import com.example.primaryschoolmanagement.vo.CourseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @PostMapping(value = "")
    public R addCourse(@RequestBody SubjectTeacherRelationDTO dto){
        System.out.println(dto);
        if (dto == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"数据为空");
        }
        int row = courseService.addcourse(dto);
        return row > 0 ?R.ok():R.er();
    }
//    @PostMapping(value = "")
//    public R createCourse(@RequestBody Course course){
//
//        Boolean iscreate = null;
//        try {
//            iscreate = courseService.createCourse(course);
//            if (!iscreate){
//                return R.er();
//            }
//        }catch (Exception e){
//            return R.er(400,"新增失败"+e.getMessage());
//        }
//
//        return R.ok();
//    }
    @PutMapping(value = "/{id}")
    public R update(@RequestBody Course course){
        if(course == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"传递参数为空");
        }
        if (!courseService.updateCourse(course)){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"更新失败");
        };
        return  R.ok();
    }
    @DeleteMapping
    public R delete(@RequestBody SubjectTeacherRelationDTO dto){
        if(dto == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"删除的课程为空");
        }
        int row = courseService.deleteCourse(dto);
        return row > 0 ? R.ok():R.er();
    }
//    @DeleteMapping(value = "/{id}")
//    public R delete(@RequestBody Integer id){
//        if (id == null){
//            throw new ApiException(HttpStatus.BAD_REQUEST,"传递的数据为空");
//        }
//        int row = courseService.deleteCourse(id);
//        return row > 0 ? R.ok():R.er();
//    }
    @GetMapping(value = "/{id}")
    public R getCourse(@RequestBody Integer id){
        if (id == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"传递的参数为空");
        }
        CourseVO courseVO = courseService.getCourse(id);
        return R.ok(courseVO);
    }
    @GetMapping(value = "/list")
    public R courseList(Integer subjectId){
        System.out.println(subjectId);
        if(subjectId == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"传递的参数为空1");
        }
        List<CourseVO> courseList = courseService.list(subjectId);
        return R.ok(courseList);
    }

}
