package com.platform.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "个人信息修改请求")
public class ProfileUpdateDTO {

    @Schema(description = "姓名", example = "管理员")
    @Size(max = 30, message = "姓名最多30字符")
    private String userName;

    @Schema(description = "头像URL")
    private String avatar;
}