package com.platform.server.crm.customer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "客户跟进记录响应")
public class FollowVO {

    @Schema(description = "跟进记录ID")
    private Long id;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "跟进方式")
    private String followType;

    @Schema(description = "跟进内容")
    private String followContent;

    @Schema(description = "下次跟进时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    @Schema(description = "跟进人ID")
    private Long ownerId;

    @Schema(description = "跟进人姓名")
    private String ownerName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}