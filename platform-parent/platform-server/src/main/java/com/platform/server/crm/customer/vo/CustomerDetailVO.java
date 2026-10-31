package com.platform.server.crm.customer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "客户详情响应")
public class CustomerDetailVO {

    @Schema(description = "客户基本信息")
    private CustomerVO customer;

    @Schema(description = "联系人列表")
    private List<ContactVO> contacts;

    @Schema(description = "跟进记录列表")
    private List<FollowVO> followRecords;
}