package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentDao extends BaseMapper<Student> {

}
