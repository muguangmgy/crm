package com.platform.server.crm.lead.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.common.enums.LeadStatusEnum;
import com.platform.server.crm.customer.dto.CustomerSaveDTO;
import com.platform.server.crm.customer.service.CustomerService;
import com.platform.server.crm.lead.dto.*;
import com.platform.server.crm.lead.mapper.CrmLeadMapper;
import com.platform.server.crm.lead.po.CrmLead;
import com.platform.server.crm.lead.service.CrmLeadService;
import com.platform.server.crm.lead.vo.LeadVO;
import com.platform.server.crm.opportunity.dto.OpportunitySaveDTO;
import com.platform.server.crm.opportunity.service.OpportunityService;
import com.platform.server.crm.opportunity.vo.StageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrmLeadServiceImpl implements CrmLeadService {

    private final CrmLeadMapper leadMapper;
    private final OpportunityService opportunityService;
    private final CustomerService customerService;

    @Override
    public PageVO<LeadVO> getLeadPage(LeadQueryDTO query) {
        Page<CrmLead> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<LeadVO> result = leadMapper.selectLeadPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public LeadVO getLeadDetail(Long id) {
        LeadVO leadVO = leadMapper.selectLeadById(id);
        if (leadVO == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "线索不存在");
        }
        return leadVO;
    }

    @Override
    public Long saveLead(LeadSaveDTO dto) {
        CrmLead lead;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            lead = new CrmLead();
            lead.setTenantId(UserContextHolder.getTenantId());
            lead.setStatus(LeadStatusEnum.NEW.getCode());
        } else {
            lead = leadMapper.selectById(dto.getId());
            if (lead == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "线索不存在");
            }
            // 已转商机的线索不能修改
            if (lead.getStatus() == LeadStatusEnum.CONVERTED.getCode()) {
                throw new BusinessException("已转商机的线索不能修改");
            }
        }

        // 设置字段
        lead.setCustomerId(dto.getCustomerId());
        lead.setContactId(dto.getContactId());
        lead.setLeadName(dto.getLeadName());
        lead.setSource(dto.getSource());
        if (dto.getStatus() != null) {
            lead.setStatus(dto.getStatus());
        }
        lead.setPhone(dto.getPhone());
        lead.setEmail(dto.getEmail());
        lead.setCompany(dto.getCompany());
        lead.setDescription(dto.getDescription());
        // 新增未指定负责人时，默认当前登录用户（与客户创建一致）
        if (isNew && (dto.getOwnerId() == null || dto.getOwnerId() <= 0)) {
            lead.setOwnerId(UserContextHolder.getUserId());
        } else if (dto.getOwnerId() != null && dto.getOwnerId() > 0) {
            lead.setOwnerId(dto.getOwnerId());
        }
        lead.setDeptId(UserContextHolder.getDeptId());

        if (isNew) {
            leadMapper.insert(lead);
        } else {
            leadMapper.updateById(lead);
        }

        return lead.getId();
    }

    @Override
    public void deleteLead(Long id) {
        CrmLead lead = leadMapper.selectById(id);
        if (lead == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "线索不存在");
        }
        // 已转商机的线索不能删除
        if (lead.getStatus() == LeadStatusEnum.CONVERTED.getCode()) {
            throw new BusinessException("已转商机的线索不能删除");
        }
        leadMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Long convertToOpportunity(LeadConvertDTO dto) {
        // 查询线索
        CrmLead lead = leadMapper.selectById(dto.getLeadId());
        if (lead == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "线索不存在");
        }

        // 检查状态
        if (lead.getStatus() == LeadStatusEnum.CONVERTED.getCode()) {
            throw new BusinessException("线索已转换为商机");
        }
        if (lead.getStatus() == LeadStatusEnum.INVALID.getCode()) {
            throw new BusinessException("已失效的线索不能转换为商机");
        }

        // 关联客户：优先入参，其次线索已有客户，否则按线索信息自动创建
        Long customerId = dto.getCustomerId() != null ? dto.getCustomerId() : lead.getCustomerId();
        if (customerId == null) {
            CustomerSaveDTO customerDto = new CustomerSaveDTO();
            customerDto.setCustomerType(1);
            String customerName = StringUtils.hasText(lead.getCompany()) ? lead.getCompany() : lead.getLeadName();
            customerDto.setCustomerName(customerName);
            customerDto.setPhone(lead.getPhone());
            customerDto.setEmail(lead.getEmail());
            customerDto.setSource(lead.getSource());
            customerDto.setLevel(1);
            customerDto.setStatus(1);
            customerDto.setRemark("由线索自动创建：" + lead.getLeadName());
            customerId = customerService.saveCustomer(customerDto);
        }
        // 无论客户来源，都回写到线索，保证详情可展示关联客户
        lead.setCustomerId(customerId);

        // 初始阶段：取本租户启用中的第一个非成交/失败阶段
        List<StageVO> stages = opportunityService.getActiveStageList();
        StageVO firstStage = stages.stream()
                .filter(s -> (s.getIsWin() == null || s.getIsWin() != 1)
                        && (s.getIsLost() == null || s.getIsLost() != 1))
                .findFirst()
                .orElseThrow(() -> new BusinessException("请先配置商机阶段"));

        // 创建商机
        OpportunitySaveDTO oppDto = new OpportunitySaveDTO();
        oppDto.setCustomerId(customerId);
        oppDto.setContactId(lead.getContactId());
        oppDto.setOppName(dto.getOppName() != null ? dto.getOppName() : lead.getLeadName());
        oppDto.setStageId(firstStage.getId());
        oppDto.setExpectedAmount(dto.getExpectedAmount());
        oppDto.setExpectedDate(dto.getExpectedDate());
        oppDto.setSource(lead.getSource());
        oppDto.setDescription(lead.getDescription());
        oppDto.setOwnerId(lead.getOwnerId());

        Long oppId = opportunityService.saveOpportunity(oppDto);

        // 更新线索状态
        lead.setStatus(LeadStatusEnum.CONVERTED.getCode());
        lead.setConvertedTime(LocalDateTime.now());
        lead.setConvertedOppId(oppId);
        leadMapper.updateById(lead);

        return oppId;
    }
}