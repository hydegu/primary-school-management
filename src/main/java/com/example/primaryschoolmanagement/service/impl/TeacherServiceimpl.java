package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.TeacherDao;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


@Service
public  class TeacherServiceimpl implements TeacherService {
    @Autowired
    private TeacherDao TeacherDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public R teacherList() {
        List<String> strings = this.jdbcTemplate.queryForList("select teacher_no,teacher_name,gender,birth_date from edu_teacher;", String.class);
        return R.ok(strings);
    }

    @Override
    public boolean saveBatch(Collection<Teacher> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Teacher> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Teacher> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Teacher entity) {
        return false;
    }

    @Override
    public Teacher getOne(Wrapper<Teacher> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Optional<Teacher> getOneOpt(Wrapper<Teacher> queryWrapper, boolean throwEx) {
        return Optional.empty();
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Teacher> queryWrapper) {
        return Map.of();
    }

    @Override
    public <V> V getObj(Wrapper<Teacher> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Teacher> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Teacher> getEntityClass() {
        return null;
    }
}
