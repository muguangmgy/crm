package com.platform.server.system.dicttype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "字典类型树节点")
public class DictTypeTreeVO {

    @Schema(description = "字典类型ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "0")
    private Long tenantId;

    @Schema(description = "租户名称", example = "全局")
    private String tenantName;

    @Schema(description = "父字典类型ID", example = "0")
    private Long parentId;

    @Schema(description = "字典编码", example = "STATUS")
    private String dictCode;

    @Schema(description = "字典名称", example = "状态字典")
    private String dictName;

    @Schema(description = "排序号", example = "0")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "通用状态字典")
    private String remark;

    @Schema(description = "创建时间", example = "2026-04-28 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "子节点列表")
    private List<DictTypeTreeVO> children;
}