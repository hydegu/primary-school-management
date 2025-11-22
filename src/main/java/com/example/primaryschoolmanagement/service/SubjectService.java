package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.SubjectCreateDTO;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.entity.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SubjectService extends IService<Subject> {

    PageResult<Subject> subjectList(Map<String,Object> map);

    Boolean createSubject(SubjectCreateDTO subjectCreateDTO);

    int deleteSubject(Integer id);
}
