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
        // 在这里添加需要加密的密码
        String[] passwords = {
                "123456",
                "admin123",
                "teacher123",
                "student123"
        };

        System.out.println("========== 密码加密结果 ==========");
        for (String password : passwords) {
            String encodedPassword = passwordEncoder.encode(password);
            System.out.println("明文密码: " + password);
            System.out.println("加密密码: " + encodedPassword);
            System.out.println("-----------------------------------");
        }
        System.out.println("==================================");
    }

    /**
     * 验证密码是否匹配
     * 可以用来测试明文密码和加密密码是否匹配
     */
    @Test
    void verifyPassword() {
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH"; // 替换为实际的加密密码

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("========== 密码验证结果 ==========");
        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("匹配结果: " + (matches ? "✓ 匹配成功" : "✗ 匹配失败"));
        System.out.println("==================================");
    }

    /**
     * 批量生成加密密码（用于初始化数据库用户密码）
     */
    @Test
    void generatePasswordsForDatabase() {
        // 定义用户和对应的密码
        String[][] userPasswords = {
                {"admin", "admin123"},
                {"teacher1", "teacher123"},
                {"teacher2", "teacher123"},
                {"student1", "student123"},
                {"student2", "student123"}
        };

        System.out.println("========== 数据库初始化密码 ==========");
        System.out.println("-- 可直接用于SQL INSERT语句 --\n");

        for (String[] userPassword : userPasswords) {
            String username = userPassword[0];
            String rawPassword = userPassword[1];
            String encodedPassword = passwordEncoder.encode(rawPassword);

            System.out.println("用户名: " + username);
            System.out.println("明文密码: " + rawPassword);
            System.out.println("加密密码: " + encodedPassword);
            System.out.println();
        }
        System.out.println("======================================");
    }
}
