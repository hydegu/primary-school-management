package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.dto.StudentDto;
import com.example.primaryschoolmanagement.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentDao extends BaseMapper<Student> {

    @Insert("""
        insert into sys_user
            (username,password,real_name,user_type) 
        values (#{dto.username},#{dto.password},#{dto.realName},#{dto.userType})
    """)
    int addUser(StudentDto dto);
    @Insert("""
        insert into sys_user_role
            (user_id,role_id) 
        values (
            (select id from sys_user where username = #{username})
            ,5)
    """)
    int addUserRole(String username);

    @Delete("""
        delete from sys_user_role where user_id = #{student.userId} and role_id = 5
""")
    int deleteUserRole(Long student);

}
