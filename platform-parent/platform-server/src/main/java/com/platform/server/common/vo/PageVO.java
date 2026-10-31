package com.platform.server.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页响应结果")
public class PageVO<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页数量", example = "10")
    private Long pageSize;

    @Schema(description = "总页数", example = "10")
    private Integer pages;

    public static <T> PageVO<T> of(List<T> list, Long total, Long pageNum, Long pageSize) {
        PageVO<T> vo = new PageVO<>();
        vo.setList(list);
        vo.setTotal(total);
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setPages((int) Math.ceil((double) total / pageSize));
        return vo;
    }
}