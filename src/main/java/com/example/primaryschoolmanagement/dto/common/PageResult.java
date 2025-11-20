package com.example.primaryschoolmanagement.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long total, List<T> list, Integer page, Integer size) {
        int pages = (int) Math.ceil((double) total / size);
        return PageResult.<T>builder()
                .total(total)
                .list(list)
                .page(page)
                .size(size)
                .pages(pages)
                .build();
    }
}
