package com.platform.server.system.menu.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单查询条件")
public class MenuQueryDTO extends PageQuery {

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型：1目录 2菜单 3按钮")
    private Integer menuType;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}