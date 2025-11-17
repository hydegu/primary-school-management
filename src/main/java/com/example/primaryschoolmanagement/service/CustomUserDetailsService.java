package com.example.primaryschoolmanagement.service;

import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser;
        try {
            appUser = userService.findByUserName(username);
            log.info("根据用户名加载用户: {}", appUser);
        } catch (Exception ex) {
            log.error("查询用户 {} 失败，认证中止", username, ex);
            throw new InternalAuthenticationServiceException("查询用户失败", ex);
        }

        if (appUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (!StringUtils.hasText(appUser.getPassword())) {
            throw new InternalAuthenticationServiceException("用户密码为空");
        }

        Role role = userService.selectRolesByUserId(appUser.getId());
        if (role == null) {
            throw new InternalAuthenticationServiceException("用户未配置角色");
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.getRoleCode()));
        log.info("用户 {} 加载完成，授予角色 {}", username, role);
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }
}
