package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.*;
import com.example.primaryschoolmanagement.dto.TeacherDTO;
import com.example.primaryschoolmanagement.dto.TeacherQueryDTO;
import com.example.primaryschoolmanagement.dto.subjectteacherDTO;
import com.example.primaryschoolmanagement.entity.*;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;


@Service
public  class TeacherServiceimpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {
    private final TeacherDao teacherDao;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final SubjectTeacherDao subjectTeacherDao;
    private final SubjectDao subjectDao;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public TeacherServiceimpl(TeacherDao teacherDao, UserDao userDao, UserRoleDao userRoleDao,
                              SubjectTeacherDao subjectTeacherDao, SubjectDao subjectDao) {
        this.teacherDao = teacherDao;
        this.userDao = userDao;
        this.userRoleDao =userRoleDao;
        this.subjectTeacherDao=subjectTeacherDao;
        this.subjectDao = subjectDao;
    }

    public R teacherList() {
        // 使用MyBatis-Plus查询所有未删除的教师
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getIsDeleted, false);
        List<Teacher> teachers = this.teacherDao.selectList(queryWrapper);
        return R.ok(teachers);
    }

    @Override
    public R queryByConditions(TeacherQueryDTO teacherQueryDTO) {
        Page<Teacher> page = new Page<>();
        page.setCurrent(1);
        page.setSize(5);
        // 构建查询条件
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        // 逻辑删除条件（根据实际项目调整，如不需要可删除）
        queryWrapper.eq(Teacher::getIsDeleted, false);

        // 1. 优先按姓名筛选（如果传入姓名）
        if (teacherQueryDTO.getTeacherNo() != null && !teacherQueryDTO.getTeacherNo().trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTeacherNo, teacherQueryDTO.getTeacherNo().trim());
        }

        // 2. 在姓名筛选结果的基础上，按工号进一步筛选（如果传入工号）
        if (teacherQueryDTO.getTeacherName() != null && !teacherQueryDTO.getTeacherName().trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTeacherName, teacherQueryDTO.getTeacherName().trim());
        }

        // 3. 在前两步结果的基础上，按职称进一步筛选（如果传入职称）
        if (teacherQueryDTO.getTitle() != null && !teacherQueryDTO.getTitle().trim().isEmpty()) {
            queryWrapper.eq(Teacher::getTitle, teacherQueryDTO.getTitle().trim());
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

    private Teacher findByTeacherNo(String teacherNo) {
        if (!StringUtils.hasText(teacherNo)) {
            return null;
        }
        // 条件查询：根据学号精确匹配
        return teacherDao.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getTeacherNo, teacherNo.trim())
                .last(""));
    }


    @Override
    @Transactional
    public R addTeacher(Teacher teacher){
        LocalDateTime currentTime = LocalDateTime.now();

        AppUser appuser = new AppUser();
        appuser.setUsername(teacher.getTeacherNo());
        String encodedPassword = passwordEncoder.encode("teacher123");
        appuser.setPassword(encodedPassword);
        appuser.setRealName(teacher.getTeacherName());
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
        teacher.setUserId(appuser.getId());
        int teacherrow = this.teacherDao.insert(teacher);
        if (teacherrow <= 0) {
            throw new RuntimeException("教师信息插入失败");
        }
        UserRole userrole = new UserRole();
        userrole.setUserId(appuser.getId());
//        System.out.println("title实际值：'" + teacher.getTitle() + "'");
        if("班主任".equals(teacher.getTitle())){
            userrole.setRoleId(4L);
//            System.out.println("title实际值1：'" + teacher.getTitle() + "'");
        }else{
            userrole.setRoleId(3L);
//            System.out.println("title实际值2：'" + teacher.getTitle() + "'");
        }
        int userRoleRow = this.userRoleDao.insert(userrole);
        if (userRoleRow <= 0) {
            throw new RuntimeException("用户信息插入失败");
        }

        return R.ok("信息添加成功");
    }




    @Override
    @Transactional
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
        Long appuserid = existingTeacher.getUserId();
        System.out.println("appuserid:"+appuserid);
        if (appuserid == null) {
            return R.er(400, "当前教师未关联用户信息，无法删除");
        }
        AppUser existingAppUser = userDao.selectById(appuserid);
        if (existingAppUser == null || existingAppUser.getIsDeleted()) {
            return R.er(404, "未找到关联的用户记录（ID：" + appuserid + "）");
        }
        // 3. 执行逻辑删除（更新isDeleted为true）
        //教师表
        Teacher updateTeacher = new Teacher();
        updateTeacher.setId(id);
        updateTeacher.setIsDeleted(true);
        int teacherrow = teacherDao.deleteById(updateTeacher);
        //用户表
        int appuserrow = 0;
        int userRoleRow = 0;


        AppUser updateAppUser = new AppUser();
        updateAppUser.setId(Long.valueOf(appuserid));
        updateAppUser.setIsDeleted(true);
        appuserrow = userDao.deleteById(updateAppUser);

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
     * @return
     */
    @Override
    public R updateTeacher(Integer id,Teacher teacher, AppUser appuser, UserRole userrole,  subjectteacherDTO dto) {
        // 1. 验证主键id是否存在（必须传入id才能确定更新哪条记录）
        if (id == null) {
            return R.er(400, "更新失败：教师ID不能为空");
        }

        Integer roleid = null;


        // 2. 验证要更新的教师是否存在
        Teacher existingTeacher = teacherDao.selectById(id);
        if (existingTeacher == null) {
            return R.er(404, "未找到ID为" + id + "的教师记录");
        }
        Integer userid = Math.toIntExact(existingTeacher.getUserId());
        if (userid == null) {
            return R.er(400, "更新失败：教师关联的用户ID为空");
        }

        // 3. 设置更新时间（无论是否修改其他字段，更新时间都要刷新）
        teacher.setUpdatedAt(LocalDateTime.now());

        // 4. 构建教师表更新条件
        UpdateWrapper<Teacher> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id); // 条件：根据id更新
        updateWrapper.eq("is_deleted", 0); // 补充逻辑删除条件

        // -------------- 提前声明并初始化 wrapper 和 userwrapper --------------
        UpdateWrapper<AppUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userid);
        wrapper.eq("is_deleted", 0); // 补充逻辑删除条件

        UpdateWrapper<UserRole> userwrapper = new UpdateWrapper<>();
        userwrapper.eq("user_id", userid);

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
            appuser.setPhone(teacher.getPhone());
            wrapper.set("phone", appuser.getPhone()); // 给用户表加set
        }
        if (teacher.getEmail() != null && !teacher.getEmail().isEmpty()) {
            updateWrapper.set("email", teacher.getEmail());
            appuser.setEmail(teacher.getEmail());
            wrapper.set("email", appuser.getEmail()); // 给用户表加set
        }
        if (teacher.getTitle() != null && !teacher.getTitle().isEmpty()) {
            updateWrapper.set("title", teacher.getTitle());
            // 根据职称设置角色ID
            roleid = teacher.getTitle() != null && teacher.getTitle().contains("班主任") ? 4 : 3;
            userwrapper.set("role_id", roleid); // 给角色表加set

        }
        if (teacher.getHireDate() != null) {
            updateWrapper.set("hire_date", teacher.getHireDate());
        }

        // 5. 执行教师表更新（一定有set字段，因为updated_at强制设置了）
        int teacherrow = this.teacherDao.update(null, updateWrapper);

        // 6. 执行用户表更新（关键修复：先判断getSqlSet()是否为null，再判断是否为空）
        int appuserrow = 0;
        // 三元运算符：如果getSqlSet()是null，视为无更新字段；否则判断是否为空
        boolean hasAppUserSet = wrapper.getSqlSet() != null && !wrapper.getSqlSet().isEmpty();
        if (hasAppUserSet) {
            appuserrow = this.userDao.update(null, wrapper);
        } else {
            appuserrow = 1; // 无更新字段，视为成功
        }

        //  执行用户角色表更新（同样处理null情况）
        int userrolerow = 0;
        boolean hasUserRoleSet = userwrapper.getSqlSet() != null && !userwrapper.getSqlSet().isEmpty();
        if (hasUserRoleSet) {
            userrolerow = this.userRoleDao.update(null, userwrapper);
        } else {
            userrolerow = 1; // 无更新字段，视为成功
        }

        // 根据结果返回信息
        boolean success = teacherrow > 0 && appuserrow > 0 && userrolerow > 0;
        return success ? R.ok("更新成功") : R.er(400, "更新失败：未修改任何有效数据");
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
