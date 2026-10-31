package com.platform.server.crm.product.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体
 */
@Data
@TableName("crm_product")
public class CrmProduct {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 分类ID */
    private Long categoryId;

    /** 产品名称 */
    private String productName;

    /** 产品编号（自动生成） */
    private String productNo;

    /** 产品编码 */
    private String productCode;

    /** 单位 */
    private String unit;

    /** 规格 */
    private String spec;

    /** 型号 */
    private String model;

    /** 标准价格 */
    private BigDecimal price;

    /** 最低价格 */
    private BigDecimal minPrice;

    /** 成本价格 */
    private BigDecimal costPrice;

    /** 产品描述 */
    private String description;

    /** 产品图片 */
    private String imageUrl;

    /** 状态：0停售 1在售 */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}