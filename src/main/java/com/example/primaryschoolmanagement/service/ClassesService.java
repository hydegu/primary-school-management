package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;

public interface ClassesService extends IService<Classes> {
    R classesList();
    R addclasses(Classes classes);
    R deleteclasses(Classes classes);
    R updateclasses(Classes classes);
}
