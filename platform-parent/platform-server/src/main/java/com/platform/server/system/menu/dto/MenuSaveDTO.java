package com.platform.server.system.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "菜单保存请求")
public class MenuSaveDTO {

    @Schema(description = "菜单ID（修改时必填）")
    private Long id;

    @Schema(description = "父菜单ID（0为顶级）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户管理")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称最多50字符")
    private String menuName;

    @Schema(description = "菜单类型：1目录 2菜单 3按钮", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @Schema(description = "路由路径", example = "/system/user")
    @Size(max = 200, message = "路由路径最多200字符")
    private String path;

    @Schema(description = "组件路径", example = "system/user/index")
    @Size(max = 200, message = "组件路径最多200字符")
    private String component;

    @Schema(description = "权限标识", example = "system:user:list")
    @Size(max = 100, message = "权限标识最多100字符")
    private String perms;

    @Schema(description = "图标", example = "user")
    @Size(max = 50, message = "图标最多50字符")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "是否可见：0隐藏 1显示", example = "1")
    private Integer visible;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;
}