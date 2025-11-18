package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.StudentDao;
import com.example.primaryschoolmanagement.entity.Student;

import com.example.primaryschoolmanagement.service.StudentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentDao,Student> implements StudentService {

    @Resource
    private StudentDao studentDao;

    @Resource
    private CacheManager cacheManager;

    @Override
    @Cacheable(cacheNames = "students:profile", key = "#studentNo", unless = "#result == null")
    public Student findByStudentNo(String studentNo) {
        if (!StringUtils.hasText(studentNo)) {
            return null;
        }
        // 条件查询：根据学号精确匹配
        return studentDao.selectOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, studentNo.trim())
                .last(""));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createStudent(Student dto) {
        // 1. 校验学号唯一性
        String studentNo = dto.getStudentNo().trim();
        Student existStudent = findByStudentNo(studentNo);
        if (existStudent != null) {
            throw new ApiException(HttpStatus.CONFLICT, "学号已存在：" + studentNo);
        }

        // 2. 转换DTO为实体
        Student student = new Student()
                .setUserId(dto.getUserId())
                .setStudentNo(studentNo)
                .setStudentName(dto.getStudentName().trim())
                .setGender(dto.getGender())
                .setBirthDate(dto.getBirthDate())
                .setIdCard(dto.getIdCard())
                .setClassId(dto.getClassId())
                .setGradeId(dto.getGradeId());

        // 3. 保存到数据库
        int rows = studentDao.insert(student);
        if (rows <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "创建学生失败");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "students:profile", key = "#dto.studentNo")
    public int updateStudent(Student dto) {
        if(dto == null || !StringUtils.hasText(dto.getStudentNo())){
            throw new ApiException(HttpStatus.BAD_REQUEST,"修改人员不能为空");
        }
        String studentNo = dto.getStudentNo();
        Student student = findByStudentNo(studentNo);
        if (student == null || !student.getIsDeleted()){
            throw new ApiException(HttpStatus.NOT_FOUND, "查无此人：" + studentNo);
        }

        //更新修改的字段
        Boolean hasChange = false;
        Student updateStudent = new Student();
        if(StringUtils.hasText(dto.getStudentName())){
            String newStudent = dto.getStudentName().trim();
            if(!dto.getStudentName().equals(student.getStudentName())){
                updateStudent.setStudentName(newStudent);
                hasChange = true;
            }
        }
        if(dto.getGender() != null && dto.getGender() != student.getGender()){
            updateStudent.setGender(dto.getGender());
            hasChange = true;
        }
        if(dto.getBirthDate() != null && dto.getBirthDate() != student.getBirthDate()){
            updateStudent.setBirthDate(dto.getBirthDate());
            hasChange = true;
        }
        if(StringUtils.hasText(dto.getIdCard())){
            String newStudent = dto.getIdCard().trim();
            String oldStudent = student.getIdCard().trim();
            if(oldStudent == null){
                updateStudent.setIdCard(newStudent);
                hasChange = true;
            }else if(!newStudent.equals(oldStudent)) {
                updateStudent.setIdCard(newStudent);
                hasChange = true;
            }
        }

        if(dto.getClassId() != null && dto.getClassId() != student.getClassId()){
            updateStudent.setClassId(dto.getClassId());
            hasChange = true;
        }
        if(dto.getGradeId() != null && dto.getGradeId() != student.getGradeId()){
            updateStudent.setGradeId(dto.getGradeId());
            hasChange = true;
        }
        if(!hasChange){
            return 0;
        }
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_no",studentNo)
                    ;
        int row = studentDao.update(updateStudent,updateWrapper);
        if (row <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "更新学生失败");
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "students:profile", key = "#id")
    public int delete(Integer id) {
        if(id == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"东西呢，老弟");
        }

        int row = studentDao.deleteById(id);
        System.out.println(row);
        if(row <= 0){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"：请求失败");
        }
        return row;
    }
}
