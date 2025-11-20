package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据访问层
 */
@Mapper
public interface RoleDao extends BaseMapper<Role> {

    /**
     * 根据角色ID查询该角色下的所有用户ID
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("""
        SELECT ur.user_id
        FROM sys_user_role ur
        WHERE ur.role_id = #{roleId}
    """)
    List<Long> selectUserIdsByRoleId(Long roleId);
}
