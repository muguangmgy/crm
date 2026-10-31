package com.platform.server.common.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询参数")
public class PageQuery {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "是否升序", example = "true")
    private Boolean isAsc = true;

    @Schema(description = "数据权限SQL条件（由切面自动注入）", hidden = true)
    private String dataScope;
}