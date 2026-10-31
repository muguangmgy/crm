package com.platform.server.crm.lead.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.lead.dto.*;
import com.platform.server.crm.lead.vo.LeadVO;

public interface CrmLeadService {

    /** 线索分页查询 */
    PageVO<LeadVO> getLeadPage(LeadQueryDTO query);

    /** 线索详情 */
    LeadVO getLeadDetail(Long id);

    /** 保存线索（新增/修改） */
    Long saveLead(LeadSaveDTO dto);

    /** 删除线索 */
    void deleteLead(Long id);

    /** 线索转商机 */
    Long convertToOpportunity(LeadConvertDTO dto);
}