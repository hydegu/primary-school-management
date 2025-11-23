package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.BusinessException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dao.ClassesDao;
import com.example.primaryschoolmanagement.dao.StudentDao;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.ClassesService;
import com.example.primaryschoolmanagement.vo.ClassesVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesDao,Classes> implements ClassesService {
    private static final Logger log = LoggerFactory.getLogger(ClassesServiceImpl.class);

    private final ClassesDao classesDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StudentDao studentDao; // 需在类中定义StudentDao的Autowired注入
    @Autowired
    private TeacherDao teacherDao;

    public ClassesServiceImpl(ClassesDao classesDao) {
        this.classesDao = classesDao;
    }
    @Override
    public R classesList(int page, int size) {
        // 创建分页对象
        Page<Classes> pageParam = new Page<>(page, size);

        // 创建查询条件，过滤已删除记录
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classes::getIsDeleted, 0);
        queryWrapper.orderByDesc(Classes::getId);

        // 执行分页查询
        IPage<Classes> classesPage = this.classesDao.selectPage(pageParam, queryWrapper);

        return R.ok(classesPage);
    }

    @Override
    public R searchClasses(String classNo, String className, String headTeacherName, int page, int size) {
        // 创建分页对象
        Page<ClassesVO> pageParam = new Page<>(page, size);

        // 执行分页模糊查询
        IPage<ClassesVO> classesPage = this.classesDao.searchClasses(pageParam, classNo, className, headTeacherName);

        return R.ok(classesPage);
    }

    @Override
    public R addclasses(Classes classes) {
        System.out.println("添加成功");
        Date currentTime = new Date();
        classes.setCreatedAt(currentTime);
        classes.setUpdatedAt(currentTime);
        int row = this.classesDao.insert(classes);
        return row>0?R.ok():R.er();
    }

    @Override
    public R deleteclasses(Integer id) {
//        // 1. 获取id并校验非空
//        Integer id = classes.getId();
//        if (id == null) {
//            System.out.println("删除失败：传入的id为null");
//            return R.er();
//        }

        // 2. 先查询记录是否存在（关键排查步骤）
        Classes existingClasses = classesDao.selectById(id);
        if (existingClasses == null) {
            System.out.println("删除失败：未找到ID为" + id + "的班级记录");
            return R.er();
        }

        // 3. 执行删除操作
        Classes updateClasses = new Classes();
        updateClasses.setId(id);
        updateClasses.setIsDeleted(Integer.valueOf(1));
        int row = this.classesDao.updateById(updateClasses);
        System.out.println("删除ID为" + id + "的班级，影响行数：" + row);

        // 4. 根据结果返回明确信息
        if (row > 0) {
            return R.ok("删除成功：已删除ID为" + id + "的班级");
        } else {
            // 若走到这里，可能是逻辑删除或其他异常
            return R.er();
        }
    }

    @Override
    public R updateclasses(Long id,Classes classes) {
        // 1. 验证主键id是否存在（必须传入id才能确定更新哪条记录）
        if (id == null) {
            return R.er();
        }

        // 2. 验证要更新的记录是否存在
        Classes existingClasses = classesDao.selectById(id);
        if (existingClasses == null) {
            return R.er();
        }

        // 3. 设置更新时间（无论是否修改其他字段，更新时间都要刷新）
        classes.setUpdatedAt(new Date());

        // 4. 使用条件更新，仅更新非空字段（核心逻辑）
        UpdateWrapper<Classes> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id); // 条件：根据id更新

        // 逐个判断字段是否为非空，非空则加入更新条件
        if (classes.getClassNo() != null && !classes.getClassNo().trim().isEmpty()) {
            updateWrapper.set("class_no", classes.getClassNo());
        }
        if (classes.getClassName() != null && !classes.getClassName().trim().isEmpty()) {
            updateWrapper.set("class_name", classes.getClassName());
        }
        if (classes.getGradeId() != null) {
            updateWrapper.set("grade_id", classes.getGradeId());
        }
        // 班主任变更的业务验证
        if (classes.getHeadTeacherId() != null) {
            Integer oldHeadTeacherId = existingClasses.getHeadTeacherId();
            Integer newHeadTeacherId = classes.getHeadTeacherId();

            // 如果班主任发生变化，需要进行业务验证
            if (!Objects.equals(oldHeadTeacherId, newHeadTeacherId)) {
                // 如果新班主任不是0（0表示清除班主任）
                if (newHeadTeacherId > 0) {
                    // 1. 验证新班主任是否存在
                    Teacher teacher = teacherDao.selectById(newHeadTeacherId);
                    if (teacher == null || teacher.getIsDeleted()) {
                        throw new BusinessException("教师ID " + newHeadTeacherId + " 不存在或已删除");
                    }

                    // 2. 验证新班主任是否已经负责其他班级（一个班主任只能负责一个班级）
                    LambdaQueryWrapper<Classes> query = new LambdaQueryWrapper<>();
                    query.eq(Classes::getHeadTeacherId, newHeadTeacherId)
                            .eq(Classes::getIsDeleted, 0)
                            .ne(Classes::getId, id);
                    long count = classesDao.selectCount(query);
                    if (count > 0) {
                        throw new BusinessException("教师 " + teacher.getTeacherName() + " 已担任其他班级的班主任，一个班主任只能负责一个班级");
                    }

                    // 3. 可选验证：检查教师职称是否为"班主任"（警告而非阻止）
                    if (!"班主任".equals(teacher.getTitle())) {
                        log.warn("教师 {} (ID: {}) 的职称为 {}，不是班主任，但被分配为班级 {} 的班主任",
                                teacher.getTeacherName(), newHeadTeacherId, teacher.getTitle(), id);
                    }
                    log.info("班级 {} 的班主任从 {} 更新为 {} ({})", id, oldHeadTeacherId, newHeadTeacherId, teacher.getTeacherName());
                } else {
                    log.info("班级 {} 清除班主任关联（原班主任ID: {}）", id, oldHeadTeacherId);
                }
            }
            updateWrapper.set("head_teacher_id", classes.getHeadTeacherId());
        }
        if (classes.getClassroom() != null && !classes.getClassroom().trim().isEmpty()) {
            updateWrapper.set("classroom", classes.getClassroom());
        }
        if (classes.getMaxStudents() != null) {
            updateWrapper.set("max_students", classes.getMaxStudents());
        }
        if (classes.getCurrentStudents() != null) {
            updateWrapper.set("current_students", classes.getCurrentStudents());
        }
        if (classes.getSchoolYear() != null && !classes.getSchoolYear().trim().isEmpty()) {
            updateWrapper.set("school_year", classes.getSchoolYear());
        }
        if (classes.getStatus() != null) {
            updateWrapper.set("status", classes.getStatus());
        }
        if (classes.getRemark() != null && !classes.getRemark().trim().isEmpty()) {
            updateWrapper.set("remark", classes.getRemark());
        }
        if (classes.getIsDeleted() != null) {
            updateWrapper.set("is_deleted", classes.getIsDeleted());
        }

        // 强制更新时间（已设置，直接加入）
        updateWrapper.set("updated_at", classes.getUpdatedAt());

        // 5. 执行更新操作（只更新非空字段）
        int row = this.classesDao.update(null, updateWrapper);

        // 6. 根据结果返回信息
        return row > 0 ? R.ok("班级信息更新成功") : R.er();
    }

    @Override
    public R getclassById(Integer id) {
        if(id==null){
            return R.er(400, "班级ID不能为空");
        }
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classes::getId,id);
        queryWrapper.eq(Classes::getIsDeleted, false);
        Classes classes = this.classesDao.selectOne(queryWrapper);
        if (classes != null) {
            return R.ok(classes);
        } else {
            return R.er(404, "未找到ID为 " + id + " 的班级记录");
        }
    }

    //班级学生列表
    @Override
    public List<Student> classStudent(Integer id) {
        if(id==null){
            return (List<Student>) R.er(400, "班级ID不能为空");
        }
        return this.classesDao.classStudent(id);
    }

    @Override
    public Teacher classheadteacher(Integer id) {
        return null;
    }

}
