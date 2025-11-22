package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.service.CourseService;
import com.example.primaryschoolmanagement.service.ScheduleService;
import com.example.primaryschoolmanagement.vo.CourseVO;
import com.example.primaryschoolmanagement.vo.ScheduleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private ScheduleService scheduleService;

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
    @DeleteMapping(value = "/{id}")
    public R delete(@RequestBody Integer teacherId){

        return null;
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
    // 6.1 课程列表 - 查询指定班级的课表
    @GetMapping(value = "/list")
    public R courseList(@RequestParam Integer classId){
        if (classId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "班级ID不能为空");
        }
        List<ScheduleVO> scheduleList = scheduleService.getScheduleByClassId(classId);
        return R.ok(scheduleList);
    }

}
