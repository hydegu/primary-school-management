package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.dao.StudentDao;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dto.StudentDto;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Student;

import com.example.primaryschoolmanagement.service.StudentService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentDao,Student> implements StudentService {

    @Resource
    private StudentDao studentDao;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private UserDao userDao;

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
    public int createStudent(StudentDto dto) {
        // 1. 校验学号唯一性
        String studentNo = dto.getStudentNo().trim();
        Student existStudent = findByStudentNo(studentNo);
        if (existStudent != null) {
            throw new ApiException(HttpStatus.CONFLICT, "学号已存在：" + studentNo);
        }

        // 2. 转换DTO为实体

        AppUser appUser = new AppUser()
                .setUsername(dto.getStudentNo())
                .setPassword("123456")
                .setUserType(3)
                .setRealName(dto.getStudentName());

        // 3. 保存到数据库

        int row1 = userDao.insert(appUser);
        if (row1 <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "添加用户失败");
        }
        Student student = new Student()
                .setUserId(appUser.getId())
                .setStudentNo(studentNo)
                .setStudentName(dto.getStudentName().trim())
                .setGender(dto.getGender())
                .setBirthDate(dto.getBirthDate())
                .setIdCard(dto.getIdCard())
                .setClassId(dto.getClassId())
                .setGradeId(dto.getGradeId());
        int rows = studentDao.insert(student);
        if (rows <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "创建学生失败");
        }
        int row2 = studentDao.addUserRole(studentNo);
        if (row2 <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "添加用户角色失败");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "students:profile", key = "#dto.id")
    public boolean updateStudent(Student dto) {
        if (dto == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "修改人员不能为空");
        }

        Student student = studentDao.selectById(dto.getId());
        if (student == null || student.getIsDeleted()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "查无此人");
        }
        //用户表
        Long id = student.getUserId();
        if (dto.getStudentName() != null){
            int row2 = studentDao.updateUser2(dto.getStudentName(),id);
        }

        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getId,dto.getId());
        //更新修改的字段
        Boolean hasChange = false;
        if(dto.getStudentName() != null){
            updateWrapper.set(Student::getStudentName,dto.getStudentName().trim());
            hasChange = true;
        }
        if(dto.getStudentNo() != null){
            updateWrapper.set(Student::getStudentNo,dto.getStudentNo());
            hasChange =true;
        }
        if (dto.getGender() != null && dto.getGender() != student.getGender()) {
            updateWrapper.set(Student::getGender,dto.getGender());
            hasChange = true;
        }
        if (dto.getBirthDate() != null && dto.getBirthDate() != student.getBirthDate()) {
            updateWrapper.set(Student::getBirthDate,dto.getBirthDate());
            hasChange = true;
        }
        if (StringUtils.hasText(dto.getIdCard())) {
            String newStudent = dto.getIdCard().trim();
            String oldStudent = student.getIdCard().trim();
            if (oldStudent == null) {
                updateWrapper.set(Student::getIdCard,dto.getIdCard());
                hasChange = true;
            } else if (!newStudent.equals(oldStudent)) {
                updateWrapper.set(Student::getIdCard,dto.getIdCard());
                hasChange = true;
            }
        }

        if (dto.getClassId() != null && dto.getClassId() != student.getClassId()) {
            updateWrapper.set(Student::getClassId,dto.getClassId());
            hasChange = true;
        }
        if (dto.getGradeId() != null && dto.getGradeId() != student.getGradeId()) {
            updateWrapper.set(Student::getGradeId,dto.getGradeId());
            hasChange = true;
        }
        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "students:profile", key = "#id")
    public int delete(Integer id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户Id为空");
        }
        Student student = studentDao.selectOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getUserId, id));
        Long userId = student.getUserId();
        if (userId == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "学生记录未关联用户ID，无法删除关联用户");
        }
        int row2 = userDao.deleteById(userId);
        int row = studentDao.deleteById(id);
        int row3 = studentDao.deleteUserRole(userId);
        if (row <= 0) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "：请求失败");
        }
        return row;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Student> list(Map<String,Object> map) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getIsDeleted,0);
        if(map == null){
            return studentDao.selectList(queryWrapper);
        }
        if(map.get("studentNo") != null){
            queryWrapper.eq(Student::getStudentNo,map.get("studentNo"));
        }
        if(map.get("studentName") != null){
            queryWrapper.eq(Student::getStudentName,map.get("studentName"));
        }
        if(map.get("gender") != null){
            queryWrapper.eq(Student::getGender,map.get("gender"));
        }
        if(map.get("IdCard") != null){
            queryWrapper.eq(Student::getIdCard,map.get("IdCard"));
        }
        if(map.get("classId") != null){
            queryWrapper.eq(Student::getClassId,map.get("classId"));
        }
        if(map.get("gradeId") != null){
            queryWrapper.eq(Student::getGradeId,map.get("gradeId"));
        }
        return studentDao.selectList(queryWrapper);
    }

}
