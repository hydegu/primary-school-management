package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.dao.CourseDao;
import com.example.primaryschoolmanagement.dto.SubjectTeacherRelationDTO;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.service.CourseService;
import com.example.primaryschoolmanagement.vo.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseDao, Course> implements CourseService {


    @Resource
    private CourseDao courseDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "courses:profile", key = "#course")
    public boolean createCourse(Course course) {
        if(course.getStatus() == null){
            course.setStatus(1);
        }
        if(course.getIsDeleted() == null){
            course.setIsDeleted(false);
        }
        return save(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "courses:profile", key = "#course.id")

    public boolean updateCourse(Course course) {
        Course existingCourse = getById(course.getId());

        if(existingCourse == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"课程不存在");
        }
        if(existingCourse.getStatus() == 0){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"课程已经停用");
        }
        if(existingCourse.getStatus() == 2){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"课程已经结束");
        }

        LambdaUpdateWrapper<Course> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Course::getId,course.getId());
        if(StringUtils.hasText(course.getCourseName())){
            updateWrapper.set(Course::getCourseName,course.getCourseName());
        }
        if(course.getSubjectId() != null){
            updateWrapper.set(Course::getSubjectId,course.getSubjectId());
        }
        if(course.getClassId() != null){
            updateWrapper.set(Course::getClassId,course.getClassId());
        }
        if(StringUtils.hasText(course.getSemester())){
            updateWrapper.set(Course::getSemester,course.getSemester());
        }
        if(course.getStatus() != null){
            updateWrapper.set(Course::getStatus,course.getStatus());
        }
        if(course.getTeacherId() != null){
            updateWrapper.set(Course::getTeacherId,course.getTeacherId());
        }

        return update(updateWrapper);
    }

    //删除中间表
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "courses:profile", key = "#dto")
    public int deleteCourse(SubjectTeacherRelationDTO dto) {
        if(dto.getSubjectId() == null || dto.getTeacherId()==null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"删除的课程为空");
        }
        int row = courseDao.delete(dto);
        if (row <= 0){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"删除失败");
        }
        return row;
    }
    //删除课程表中的数据
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    @CacheEvict(cacheNames = "courses:profile", key = "#id")
//    public int deleteCourse(Integer id) {
//        Course existCourse = getById(id);
//        if(existCourse == null){
//            throw new ApiException(HttpStatus.BAD_REQUEST,"删除的课程为空");
//        }
//        int row = courseDao.deleteById(id);
//        return row;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "courses:profile", key = "#id")
    public CourseVO getCourse(Integer id) {
        if(id == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"查询的课程为空");
        }
        CourseVO courseVO = courseDao.getCourse(id);
        if (courseVO == null){
            return null;
        }
        return courseVO;
    }

    @Override
    @Cacheable(cacheNames = "courses:profile", key = "#subjectId")
    public List<CourseVO> list(Integer subjectId) {
        if(subjectId == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"科目Id不能为空");
        }
        List<CourseVO> courseList = courseDao.courseList(subjectId);
        return  courseList == null ? List.of() : courseList;
    }

    /**
     * 查询课程列表 - subjectId和classId可选
     * 支持按科目和/或班级筛选
     */
    @Override
    public List<CourseVO> listCourses(Integer subjectId, Integer classId) {
        List<CourseVO> courseList = courseDao.courseListWithFilters(subjectId, classId);
        return courseList == null ? List.of() : courseList;
    }

    //根据课程添加仅老师
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "courses:profile", key = "#dto")
    public int addcourse(SubjectTeacherRelationDTO dto) {
        if(dto == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"新增数据是空的");
        }
        int row = courseDao.addCourse(dto);
        if(row <= 0){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"新增失败");
        }
        return row;
    }

}
