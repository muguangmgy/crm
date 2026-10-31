package com.platform.server.crm.opportunity.service;

import com.platform.server.crm.opportunity.dto.OpportunityCloseDTO;
import com.platform.server.crm.opportunity.dto.OpportunityQueryDTO;
import com.platform.server.crm.opportunity.dto.OpportunitySaveDTO;
import com.platform.server.crm.opportunity.dto.StageSaveDTO;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import com.platform.server.crm.opportunity.vo.StageVO;
import com.platform.server.common.vo.PageVO;

import java.util.List;

public interface OpportunityService {

    /** 商机分页查询 */
    PageVO<OpportunityVO> getOpportunityPage(OpportunityQueryDTO query);

    /** 商机详情 */
    OpportunityVO getOpportunityById(Long id);

    /** 保存商机（新增/修改） */
    Long saveOpportunity(OpportunitySaveDTO dto);

    /** 删除商机 */
    void deleteOpportunity(Long id);

    /** 推进商机阶段 */
    void advanceOpportunity(Long id, Long nextStageId);

    /** 商机成交/失败 */
    void closeOpportunity(Long id, OpportunityCloseDTO dto);

    /** 获取所有阶段列表 */
    List<StageVO> getStageList();

    /** 获取启用的阶段列表（用于下拉选择） */
    List<StageVO> getActiveStageList();

    /** 保存阶段（新增/修改） */
    Long saveStage(StageSaveDTO dto);

    /** 删除阶段 */
    void deleteStage(Long id);

    /** 根据阶段ID获取阶段名称 */
    String getStageName(Long stageId);
}