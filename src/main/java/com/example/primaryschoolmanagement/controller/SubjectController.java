package com.example.primaryschoolmanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.SubjectDao;
import com.example.primaryschoolmanagement.dto.SubjectCreateDTO;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.service.SubjectService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/subject")
public class SubjectController {

    @Resource
    private SubjectService subjectService;


    @GetMapping(value = "/list")
    public R subjectList(Map<String,Object> map){
        PageResult<Subject> subjectList = subjectService.subjectList(map);
        if(subjectList == null){
            throw new ApiException(HttpStatus.NOT_FOUND,"列表为空");
        }
        return R.ok(subjectList);
    }
    /**
     * 创建科目（支持封面上传）
     * 前端请求格式：multipart/form-data
     */
    @PostMapping
    public R createSubject(@Valid @ModelAttribute SubjectCreateDTO dto) {
        boolean success = subjectService.createSubject(dto);
        return R.ok();
    }
    @DeleteMapping(value = "/{id}")
    public R deleteSubject(@PathVariable Integer id){
        if(id == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"请输入科目id");
        }
        int row = subjectService.deleteSubject(id);
        return row > 0 ? R.ok():R.er();
    }
}
