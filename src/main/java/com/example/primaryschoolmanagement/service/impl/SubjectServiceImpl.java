package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.dao.SubjectDao;
import com.example.primaryschoolmanagement.dto.SubjectCreateDTO;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.service.FileStorageService;
import com.example.primaryschoolmanagement.service.SubjectService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectDao,Subject> implements SubjectService {

    @Resource
    private SubjectDao subjectDao;

    private final FileStorageService fileStorageService;
    public SubjectServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }


    @Override
    public PageResult<Subject> subjectList(Map<String,Object> map) {
        // 提取分页参数
        Integer page = ObjectUtils.isEmpty(map.get("page")) ? 1 : Integer.parseInt(map.get("page").toString());
        Integer size = ObjectUtils.isEmpty(map.get("size")) ? 10 : Integer.parseInt(map.get("size").toString());

        // 分页参数检查
        if(page <= 0)page = 1;
        if(size < 1) size = 10;
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subject::getIsDeleted,0);
        IPage<Subject> page1 = new Page<>(page,size);
        IPage<Subject> subjectList = subjectDao.selectPage(page1,queryWrapper);
        return PageResult.of(
                subjectList.getTotal(),
                subjectList.getRecords(),
                page,
                size
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "subject:profile", key = "#dto.subjectName")
    // 3. 添加@Valid触发DTO参数校验（如@NotBlank），配合DTO中的JSR-380注解
    public Boolean createSubject(@Valid SubjectCreateDTO dto) {
        // 3.1 基础参数校验（补充DTO未覆盖的关联校验）
        if (dto.getSubjectName() == null && dto.getSubjectCode() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "科目名称和编码不能同时为空");
        }

        // 3.2 封面上传时，校验subjectId不为空（避免传递null给文件上传方法）
        if (dto.getCoverFile() != null && !dto.getCoverFile().isEmpty() && dto.getId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "上传封面时，科目ID不能为空");
        }

        // 3.3 业务校验：科目名称和编码唯一性（避免数据库唯一索引冲突）
        checkSubjectUnique(dto.getSubjectName(), dto.getSubjectCode());

        // 4. 处理封面上传（复用文件存储服务）
        String subjectCoverUrl = null;
        try {
            if (dto.getCoverFile() != null && !dto.getCoverFile().isEmpty()) {
                subjectCoverUrl = fileStorageService.storeCover(dto.getCoverFile(), dto.getId());
            }
        } catch (Exception e) {
            // 捕获文件上传异常，给出明确提示
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "封面上传失败：" + e.getMessage());
        }

        // 5. 构建Subject实体（完善默认值逻辑）
        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setSubjectName(dto.getSubjectName().trim()); // 去除首尾空格，避免无效空格
        subject.setSubjectCode(dto.getSubjectCode().trim());
        subject.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        subject.setIsDeleted(dto.getIsDeleted() == null ? false : dto.getIsDeleted());
        subject.setAvatar(subjectCoverUrl);

        // 6. 保存到数据库
        boolean saveSuccess = save(subject);

        // 7. 事务补偿：保存失败时删除已上传的封面（避免垃圾文件）
        if (!saveSuccess && subjectCoverUrl != null) {
            boolean deleteSuccess = fileStorageService.deleteFile(subjectCoverUrl);
            log.warn("科目创建失败，删除已上传封面：subjectId={}, url={}, 删除结果={}",
                    dto.getId(), subjectCoverUrl, deleteSuccess);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "创建科目失败，请重试");
        }

        log.info("科目创建成功：subjectId={}, subjectName={}", dto.getId(), dto.getSubjectName());
        return saveSuccess;
    }

    @Override
    public int deleteSubject(Integer id) {
        if(id == null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"该科目不存在");
        }
        int row2;
        try {
            row2 = subjectDao.deleteTeacher(id);
        }catch (Exception e){
            return 0;
        }

        int row = subjectDao.deleteById(id);
        if(row <= 0){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"删除失败");
        }
        return row;
    }

    /**
     * 新增：校验科目名称和编码唯一性
     * 若数据库存在相同名称或编码，抛出异常
     */
    private void checkSubjectUnique(String subjectName, String subjectCode) {
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<Subject>()
                .eq(Subject::getSubjectName, subjectName.trim()) // 匹配名称（去空格）
                .or() // 或匹配编码
                .eq(Subject::getSubjectCode, subjectCode.trim());

        long count = count(queryWrapper);
        if (count > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "科目名称或编码已存在，请勿重复创建");
        }
    }
}

