package com.platform.server.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户保存请求")
public class UserSaveDTO {

    @Schema(description = "用户ID（修改时必填）")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度2-50字符")
    private String userCode;

    @Schema(description = "密码（新增时必填）", example = "123456")
    private String password;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名最多50字符")
    private String userName;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Size(max = 100, message = "邮箱最多100字符")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Size(max = 20, message = "手机号最多20字符")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别：0未知 1男 2女", example = "1")
    private Integer gender;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "部门不能为空")
    private Long deptId;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}