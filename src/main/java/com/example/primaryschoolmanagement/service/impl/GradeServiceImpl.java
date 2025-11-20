package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.GradeDao;
import com.example.primaryschoolmanagement.entity.Grade;
import com.example.primaryschoolmanagement.entity.Teacher;
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
        LambdaQueryWrapper<Grade>  queryWrapper = new LambdaQueryWrapper<>();
        List<Grade> gradeList = this.gradeDao.selectList(queryWrapper);
        if (gradeList.isEmpty()) {
            return R.er();
        } else {
            return R.ok(gradeList);
        }
    }
}
