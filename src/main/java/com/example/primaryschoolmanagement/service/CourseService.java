package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.SubjectTeacherRelationDTO;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.vo.CourseVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService extends IService<Course> {

    boolean createCourse(Course course);

    boolean updateCourse(Course course);

    int deleteCourse(SubjectTeacherRelationDTO dto);

    CourseVO getCourse(Integer id);

    List<CourseVO> list(Integer subjectId);

    /**
     * 查询课程列表 - subjectId和classId可选
     * @param subjectId 科目ID，可为null
     * @param classId 班级ID，可为null
     * @return 课程列表
     */
    List<CourseVO> listCourses(Integer subjectId, Integer classId);

    int addcourse(SubjectTeacherRelationDTO dto);



}
