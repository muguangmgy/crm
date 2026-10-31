package com.platform.server.crm.customer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "客户信息响应")
public class CustomerVO {

    @Schema(description = "客户ID")
    private Long id;

    @Schema(description = "客户编号")
    private String customerNo;

    @Schema(description = "客户类型：1企业 2个人")
    private Integer customerType;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "行业")
    private String industry;

    @Schema(description = "企业规模")
    private String companySize;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "网站")
    private String website;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "职业")
    private String occupation;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "客户来源")
    private String source;

    @Schema(description = "客户等级：1普通 2重要 3VIP")
    private Integer level;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "池类型：1私有池 2公海池")
    private Integer poolType;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人姓名")
    private String ownerName;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "锁定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lockTime;

    @Schema(description = "下次跟进时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}