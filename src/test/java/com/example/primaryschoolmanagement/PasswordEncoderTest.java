package com.example.primaryschoolmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密测试类
 * 用于生成BCrypt加密后的密码字符串
 */
@SpringBootTest
class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 生成加密密码
     * 可以修改passwords数组中的明文密码，运行测试后会输出加密后的字符串
     */
    @Test
    void generateEncodedPassword() {
            String password = "admin123";
            String encodedPassword = passwordEncoder.encode(password);
            System.out.println("明文密码: " + password);
            System.out.println("加密密码: " + encodedPassword);
            System.out.println("-----------------------------------");
    }

    /**
     * 验证密码是否匹配
     * 可以用来测试明文密码和加密密码是否匹配
     */
    @Test
    void verifyPassword() {
        String rawPassword = "admin123";
        String encodedPassword = "$2a$10$ZUh.a8caUHfFwRkAtVep8eS6o75W2B1ubK4bPLWKvnlBH26wlh/tS"; // 替换为实际的加密密码

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("========== 密码验证结果 ==========");
        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("匹配结果: " + (matches ? "✓ 匹配成功" : "✗ 匹配失败"));
        System.out.println("==================================");
    }

}
