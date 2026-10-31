package com.platform.server.system.menu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "菜单信息响应")
public class MenuVO {

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    @Schema(description = "菜单类型：1目录 2菜单 3按钮", example = "2")
    private Integer menuType;

    @Schema(description = "路由路径", example = "/system/user")
    private String path;

    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    @Schema(description = "权限标识", example = "system:user:list")
    private String perms;

    @Schema(description = "图标", example = "user")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "是否可见：0隐藏 1显示", example = "1")
    private Integer visible;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "子菜单列表")
    private List<MenuVO> children;
}