package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.ClassesDao;
import com.example.primaryschoolmanagement.dao.StudentDao;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesDao,Classes> implements ClassesService {
    private final ClassesDao classesDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StudentDao studentDao; // 需在类中定义StudentDao的Autowired注入

    public ClassesServiceImpl(ClassesDao classesDao) {
        this.classesDao = classesDao;
    }
    @Override
    public R classesList() {
       LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        List<Classes> classesList = this.classesDao.selectList(queryWrapper);
        if (classesList.isEmpty()) {
            return R.er();
        } else {
            return R.ok(classesList);
        }
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
            System.out.println("删除失败：未找到ID为" + id + "的教师记录");
            return R.er();
        }

        // 3. 执行删除操作
        Classes updateClasses = new Classes();
        updateClasses.setId(id);
        updateClasses.setIsDeleted(Integer.valueOf(1));
        int row = this.classesDao.updateById(updateClasses);
        System.out.println("删除ID为" + id + "的教师，影响行数：" + row);

        // 4. 根据结果返回明确信息
        if (row > 0) {
            return R.ok("删除成功：已删除ID为" + id + "的教师");
        } else {
            // 若走到这里，可能是逻辑删除或其他异常
            return R.er();
        }
    }

    @Override
    public R updateclasses(Classes classes) {
        // 1. 验证主键id是否存在（必须传入id才能确定更新哪条记录）
        Integer id = classes.getId();
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
        // 修正headTeacherId的判断（原wait()是错误调用，此处判断非空即可）
        if (classes.getHeadTeacherId() != null) {
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

    @Override
    public R classStudent(Classes classes, Student student) {
        Integer id = classes.getId();
        Integer classid = student.getClassId();
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("student.class_id",id)//学生表class_id=class表的id
                .eq("classes.id",id)
                .select(
                        "student.id",
                        "student.studentNo",
                        "student.studentName",
                        "student.gender",
                        "classes.classNo",
                        "classes.className",
                        "classes.headTeacherId"
                );



        List<Map<String, Object>> resultList = studentDao.selectMaps(wrapper);

        // 4. 处理查询结果
        if (resultList.isEmpty()) {
            return R.er(404, "该班级暂无学生数据");
        } else {
            return R.ok(resultList);
        }
    }



}
