package com.platform.server.system.dept.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "部门信息响应")
public class DeptVO {

    @Schema(description = "部门ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "父部门ID", example = "0")
    private Long parentId;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "部门路径", example = "/1/2/3")
    private String deptPath;

    @Schema(description = "负责人ID", example = "1")
    private Long leaderId;

    @Schema(description = "负责人姓名", example = "张三")
    private String leaderName;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "子部门列表")
    private List<DeptVO> children;
}