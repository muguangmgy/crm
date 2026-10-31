package com.platform.server.crm.contract.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.contract.dto.ContractQueryDTO;
import com.platform.server.crm.contract.dto.ContractSaveDTO;
import com.platform.server.crm.contract.vo.ContractVO;

public interface ContractService {

    /** 合同分页查询 */
    PageVO<ContractVO> getContractPage(ContractQueryDTO query);

    /**
     * 合同下拉选项（发票/收款等关联选择）
     * 仅租户隔离，不做负责人行级过滤
     */
    java.util.List<ContractVO> getContractOptions(Integer status);

    /** 合同详情 */
    ContractVO getContractById(Long id);

    /** 保存合同（新增/修改） */
    Long saveContract(ContractSaveDTO dto);

    /** 删除合同 */
    void deleteContract(Long id);

    /** 提交审批（草稿→审批中） */
    void submitContract(Long id);

    /** 审批通过（审批中→生效） */
    void approveContract(Long id);

    /** 审批驳回（审批中→草稿） */
    void rejectContract(Long id);

    /** 终止合同（生效→终止） */
    void terminateContract(Long id);
}