package com.example.primaryschoolmanagement;

import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrimarySchoolManagementApplicationTests {
    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        userDao.insert(new AppUser().setUsername("test").setPassword("test").setRealName("test").setUserType(1).setAvatar(null).setPhone(null).setEmail(null).setGender(1).setStatus(1));
    }

}
