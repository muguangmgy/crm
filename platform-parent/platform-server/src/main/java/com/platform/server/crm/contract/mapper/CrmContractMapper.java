package com.platform.server.crm.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.annotation.DataPermissionIgnore;
import com.platform.common.annotation.DataScope;
import com.platform.server.crm.contract.dto.ContractQueryDTO;
import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.contract.vo.ContractVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CrmContractMapper extends BaseMapper<CrmContract> {

    /** 分页查询合同列表（带关联信息） */
    @DataScope(deptAlias = "c", userAlias = "c")
    IPage<ContractVO> selectContractPage(Page<ContractVO> page, @Param("query") ContractQueryDTO query);

    /** 根据ID查询合同详情（带关联信息） */
    @DataScope(deptAlias = "c", userAlias = "c")
    ContractVO selectContractById(@Param("id") Long id);

    /**
     * 合同下拉选项（发票/收款等关联选择）
     * 跨负责人协作场景，仅按租户隔离，不做 owner 行级过滤
     */
    @DataPermissionIgnore
    List<ContractVO> selectContractOptions(@Param("status") Integer status);

    /** 更新已收款金额（内部回写，跳过行级数据权限） */
    @DataPermissionIgnore
    int updateReceivedAmount(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);

    /** 更新已开票金额（内部回写，跳过行级数据权限） */
    @DataPermissionIgnore
    int updateInvoicedAmount(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);
}