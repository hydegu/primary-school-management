package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dao.UserRoleDao;
import com.example.primaryschoolmanagement.entity.*;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public  class TeacherServiceimpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {
    private final TeacherDao teacherDao;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    public TeacherServiceimpl(TeacherDao teacherDao, UserDao userDao, UserRoleDao userRoleDao) {
        this.teacherDao = teacherDao;
        this.userDao = userDao;
        this.userRoleDao =userRoleDao;
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
        Page<Teacher> page = new Page<>();
        page.setCurrent(1);
        page.setSize(5);
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
        Page<Teacher> pageInfo = this.teacherDao.selectPage(page, queryWrapper);


        // 执行查询
//        List<Teacher> teacherList = this.teacherDao.selectList(queryWrapper);
        List<Teacher> records = pageInfo.getRecords();
        long total = pageInfo.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("teacher", records);
        return R.ok(map);

        // 处理结果
//        if (teacherList.isEmpty()) {
//            return R.er();
//        } else {
//            return R.ok(teacherList);
//        }
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
    public R addTeacher(Teacher teacher, AppUser appuser, UserRole userrole){
        LocalDateTime currentTime = LocalDateTime.now();
        teacher.setCreatedAt(currentTime);
        teacher.setUpdatedAt(currentTime);
        int teacherrow = this.teacherDao.insert(teacher);
        if (teacherrow <= 0) {
            throw new RuntimeException("教师信息插入失败");
        }
        Integer teacherId = teacher.getId();
        if(teacherId==null){
            return R.er(400, "获取教师ID失败");
        }
        appuser.setId(Long.valueOf(teacher.getUserId()));
        appuser.setUsername("teacher"+teacher.getTeacherNo());
        appuser.setPassword("teacher123");
        appuser.setUserType(2);
        appuser.setPhone(teacher.getPhone());
        appuser.setEmail(teacher.getEmail());
        appuser.setGender(teacher.getGender());
        appuser.setCreatedAt(currentTime);
        appuser.setUpdatedAt(currentTime);
        int userrow = this.userDao.insert(appuser);
        if (userrow <= 0) {
            throw new RuntimeException("用户信息插入失败");
        }
        userrole.setUserId(appuser.getId());
        if("班主任".equals(teacher.getTitle())){
            userrole.setRoleId(4L);
        }else{
            userrole.setRoleId(3L);
        }
        int userRoleRow = this.userRoleDao.insert(userrole);
        if (userRoleRow <= 0) {
            throw new RuntimeException("用户信息插入失败");
        }
        return R.ok("信息添加成功");
    }



    @Override
    public R deleteTeacher(Teacher teacher, AppUser appuser,UserRole userrole) {
        // 1. 校验id非空
        Integer teacherid = teacher.getId();
        Integer appuserid = teacher.getUserId();
        if (teacherid == null) {
            return R.er(400, "教师ID不能为空");
        }
        // 2. 查询记录是否存在
        Teacher existingTeacher = teacherDao.selectById(teacherid);
        if (existingTeacher == null) {
            return R.er(404, "未找到ID为" + teacherid + "的教师记录");
        }
        // 3. 执行逻辑删除（更新isDeleted为true）
        //教师表
        Teacher updateTeacher = new Teacher();
        updateTeacher.setId(teacherid);
        updateTeacher.setIsDeleted(true);
        int teacherrow = teacherDao.deleteById(updateTeacher);
        //用户表
        int appuserrow = 0;
        int userRoleRow = 0;

        if (appuserid == null) {
            return R.er(400, "用户ID不能为空");
        }
        AppUser existingAppUser = userDao.selectById(appuserid);
        if (existingAppUser == null) {
            return R.er(404, "未找到ID为" + appuserid + "的用户记录");
        }
        AppUser updateAppUser = new AppUser();
        updateAppUser.setId(Long.valueOf(appuserid));
        updateAppUser.setIsDeleted(true);
        appuserrow = userDao.deleteById(updateAppUser);
        if (appuserid == null) {
            return R.er(400, "角色用户ID不能为空");
        }
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", appuserid); // 按用户ID查询关联的角色记录
        List<UserRole> existingUserRoles = userRoleDao.selectList(queryWrapper);
        if (existingUserRoles == null || existingUserRoles.isEmpty()) {
            return R.er(404, "未找到用户ID为" + appuserid + "的角色关联记录");
        }
        userRoleRow = userRoleDao.delete(queryWrapper);


        // 4. 返回结果
        // 两个条件都满足才返回成功，否则失败
        return (teacherrow > 0 && appuserrow > 0 && userRoleRow > 0)
                ? R.ok("删除成功")
                : R.er(ResultCode.ERROR);
    }


    /**
     * 修改teacher user和userrole表的相关数据也会发生改变
     * @param teacher
     * @param appuser
     * @param userrole
     * @return
     */
    @Override
    public R updateTeacher(Teacher teacher,AppUser appuser,UserRole userrole) {
        // 1. 验证主键id是否存在（必须传入id才能确定更新哪条记录）
        Integer id = teacher.getId();
        Integer userid = teacher.getUserId();
        long roleid = userrole.getRoleId();
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
        UpdateWrapper<AppUser> wrapper = new UpdateWrapper<>();
        UpdateWrapper<UserRole> userwrapper = new UpdateWrapper<>();
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
        System.out.println(teacher.getPhone());
        if (teacher.getPhone() != null && !teacher.getPhone().isEmpty()) {
            updateWrapper.set("phone", teacher.getPhone());
            appuser.setPhone(teacher.getPhone());
            wrapper.set("phone", appuser.getPhone());
        }
        if (teacher.getEmail() != null && !teacher.getEmail().isEmpty()) {
            updateWrapper.set("email", teacher.getEmail());
            appuser.setEmail(teacher.getEmail());
            wrapper.set("email", appuser.getEmail());
        }
        if (teacher.getTitle() != null && !teacher.getTitle().isEmpty()) {
            updateWrapper.set("title", teacher.getTitle());
            if(teacher.getTitle().equals("班主任")){
                roleid=4;
                userwrapper.set("role_id",roleid);
            }else{
                roleid=3;
                userwrapper.set("role_id",roleid);
            }

        }
        if (teacher.getHireDate() != null) {
            updateWrapper.set("hire_date", teacher.getHireDate());
        }
        // 注意：更新时间已强制设置，这里无需重复判断
        updateWrapper.set("updated_at", teacher.getUpdatedAt());
        // 5. 执行更新操作（只更新非空字段）
        int teacherrow = this.teacherDao.update(null, updateWrapper);
        System.out.println("id: " + id + ", userid: " + userid);
        //更新用户表
        int appuserrow = 0;
        wrapper.eq("id", userid); // 条件：更新指定ID的用户
        // 检查phone字段，非空则添加更新
//            System.out.println("appuser.getphone"+appuser.getPhone());
        appuserrow = this.userDao.update(null, wrapper);

//        System.out.println("userid: " + userid + ", role_id: " + roleid);
        int userrolerow = 0;
        userwrapper.eq("user_id", userid);
        userrolerow = this.userRoleDao.update(null, userwrapper);
        // 6. 根据结果返回信息
        return (teacherrow > 0 && appuserrow > 0 && userrolerow>0)
                ? R.ok("更新")
                : R.er(ResultCode.ERROR);
    }

    /**
     * 根据科目ID获取能教该科目的教师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    @Override
    public R getTeachersBySubjectId(Long subjectId) {
        // 1. 验证科目ID是否为空
        if (subjectId == null) {
            return R.er(400, "科目ID不能为空");
        }

        // 2. 查询能教该科目的教师列表
        List<Teacher> teachers = teacherDao.findTeachersBySubjectId(subjectId);

        // 3. 返回结果
        if (teachers == null || teachers.isEmpty()) {
            return R.ok(Collections.emptyList());  // 返回空列表而不是错误
        }
        return R.ok(teachers);
    }
    /**
     * 根据用户ID获取教师信息
     * @param userId 用户ID
     * @return 教师信息
     */
    @Override
    public Teacher getTeacherByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getUserId, userId)
                .eq(Teacher::getIsDeleted, false);
        return this.teacherDao.selectOne(queryWrapper);
    }

}
