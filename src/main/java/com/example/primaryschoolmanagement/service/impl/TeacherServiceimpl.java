package com.example.primaryschoolmanagement.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public  class TeacherServiceimpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {
    private final TeacherDao teacherDao;

    public TeacherServiceimpl(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public R teacherList() {
        // 使用MyBatis-Plus查询所有未删除的教师
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getIsDeleted, false);
        List<Teacher> teachers = this.teacherDao.selectList(queryWrapper);
        return R.ok(teachers);
    }

    @Override
    public R queryByConditions(String teacherName, String teacherNo, String title) {
        // 构建查询条件
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        // 逻辑删除条件（根据实际项目调整，如不需要可删除）
        queryWrapper.eq(Teacher::getIsDeleted, false);

        // 1. 优先按姓名筛选（如果传入姓名）
        if (teacherName != null && !teacherName.trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTeacherName, teacherName.trim());
        }

        // 2. 在姓名筛选结果的基础上，按工号进一步筛选（如果传入工号）
        if (teacherNo != null && !teacherNo.trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTeacherNo, teacherNo.trim());
        }

        // 3. 在前两步结果的基础上，按职称进一步筛选（如果传入职称）
        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTitle, title.trim());
        }

        // 执行查询
        List<Teacher> teacherList = this.teacherDao.selectList(queryWrapper);

        // 处理结果
        if (teacherList.isEmpty()) {
            return R.er();
        } else {
            return R.ok(teacherList);
        }
    }

    @Override
    public R getTeacherById(Integer id) {
        // 1. 验证ID是否为空
        if (id == null) {
            return R.er(400, "教师ID不能为空");
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getId, id);
        // 查询未删除的记录
        queryWrapper.eq(Teacher::getIsDeleted, false);

        // 3. 执行查询
        Teacher teacher = this.teacherDao.selectOne(queryWrapper);

        // 4. 处理查询结果
        if (teacher != null) {
            return R.ok(teacher);
        } else {
            return R.er(404, "未找到ID为 " + id + " 的教师记录");
        }
    }


    @Override
    public R addTeacher(Teacher teacher){
        LocalDateTime currentTime = LocalDateTime.now();
        teacher.setCreatedAt(currentTime);
        teacher.setUpdatedAt(currentTime);
        teacher.setIsDeleted(false);
        int row = this.teacherDao.insert(teacher);
        return row>0?R.ok("添加成功"):R.er(ResultCode.ERROR);
    }

    @Override
    public R deleteTeacher(Integer id) {
        // 1. 校验id非空
        if (id == null) {
            return R.er(400, "教师ID不能为空");
        }

        // 2. 查询记录是否存在
        Teacher existingTeacher = teacherDao.selectById(id);
        if (existingTeacher == null) {
            return R.er(404, "未找到ID为" + id + "的教师记录");
        }

        // 3. 执行逻辑删除（更新isDeleted为true）
        Teacher updateTeacher = new Teacher();
        updateTeacher.setId(id);
        updateTeacher.setIsDeleted(true);
        updateTeacher.setUpdatedAt(LocalDateTime.now());
        int row = teacherDao.updateById(updateTeacher);

        // 4. 返回结果
        return row > 0 ? R.ok("删除成功") : R.er(ResultCode.ERROR);
    }



    @Override
    public R updateTeacher(Teacher teacher) {
        // 1. 验证主键id是否存在（必须传入id才能确定更新哪条记录）
        Integer id = teacher.getId();
        if (id == null) {
            return R.er(400, "教师ID不能为空");
        }

        // 2. 验证要更新的记录是否存在
        Teacher existingTeacher = teacherDao.selectById(id);
        if (existingTeacher == null) {
            return R.er(404, "未找到ID为" + id + "的教师记录");
        }

        // 3. 设置更新时间（无论是否修改其他字段，更新时间都要刷新）
        teacher.setUpdatedAt(LocalDateTime.now());

        // 4. 使用条件更新，仅更新非空字段（核心逻辑）
        UpdateWrapper<Teacher> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id); // 条件：根据id更新

        // 逐个判断字段是否为null，非null则加入更新条件
        if (teacher.getTeacherNo() != null && !teacher.getTeacherNo().isEmpty()) {
            updateWrapper.set("teacher_no", teacher.getTeacherNo());
        }
        if (teacher.getTeacherName() != null && !teacher.getTeacherName().isEmpty()) {
            updateWrapper.set("teacher_name", teacher.getTeacherName());
        }
        if (teacher.getGender() != null) {
            updateWrapper.set("gender", teacher.getGender());
        }
        if (teacher.getBirthDate() != null) {
            updateWrapper.set("birth_date", teacher.getBirthDate());
        }
        if (teacher.getIdCard() != null && !teacher.getIdCard().isEmpty()) {
            updateWrapper.set("id_card", teacher.getIdCard());
        }
        if (teacher.getPhone() != null && !teacher.getPhone().isEmpty()) {
            updateWrapper.set("phone", teacher.getPhone());
        }
        if (teacher.getEmail() != null && !teacher.getEmail().isEmpty()) {
            updateWrapper.set("email", teacher.getEmail());
        }
        if (teacher.getTitle() != null && !teacher.getTitle().isEmpty()) {
            updateWrapper.set("title", teacher.getTitle());
        }
        if (teacher.getHireDate() != null) {
            updateWrapper.set("hire_date", teacher.getHireDate());
        }
        // 注意：更新时间已强制设置，这里无需重复判断
        updateWrapper.set("updated_at", teacher.getUpdatedAt());

        // 5. 执行更新操作（只更新非空字段）
        int row = this.teacherDao.update(null, updateWrapper);

        // 6. 根据结果返回信息
        return row > 0 ? R.ok("更新成功") : R.er();
    }

}
