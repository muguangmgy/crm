package com.platform.server.crm.customer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "客户标签响应")
public class TagVO {

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签颜色")
    private String tagColor;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}