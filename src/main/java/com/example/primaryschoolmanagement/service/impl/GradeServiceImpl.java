package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.GradeDao;
import com.example.primaryschoolmanagement.entity.Grade;
import com.example.primaryschoolmanagement.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class GradeServiceImpl extends ServiceImpl<GradeDao, Grade> implements GradeService {

    private final GradeDao gradeDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public GradeServiceImpl(GradeDao gradeDao) {
        this.gradeDao = gradeDao;
    }


    public R gradeList() {
        System.out.println("查询成功");
        Page page = new Page();
        page.setCurrent(1);
        page.setSize(5);
        Page pageInfo = this.gradeDao.selectPage(page, null);
        // 查询多列，用RowMapper映射到Teacher对象
        List<Grade> grades = this.jdbcTemplate.query(
                "select * " +
                        "from edu_grade;",
                new RowMapper<Grade>() {
                    @Override
                    public Grade mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Grade grade = new Grade();
                        // 手动映射：数据库列名 -> 对象属性（注意字段类型匹配）
                        grade.setId(rs.getInt("id"));
                        grade.setGradeName(rs.getString("grade_name"));
                        grade.setGradeLevel(rs.getString("grade_level"));
                        grade.setSchoolYear(rs.getString("school_year"));
                        grade.setStatus(rs.getInt("status"));
                        grade.setRemark(rs.getString("remark"));
                        grade.setCreatedAt(rs.getDate("created_at"));
                        grade.setUpdatedAt(rs.getDate("updated_at"));
                        grade.setIsDeleted(rs.getInt("is_deleted"));
                        return grade;
                    }
                }
        );
        return R.ok(grades);
    }
}
