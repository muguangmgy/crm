package com.platform.server.crm.customer.service.impl;

import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.crm.common.converter.CRMConverter;
import com.platform.server.crm.customer.dto.FollowSaveDTO;
import com.platform.server.crm.customer.mapper.CustomerMapper;
import com.platform.server.crm.customer.mapper.FollowMapper;
import com.platform.server.crm.customer.po.CrmCustomer;
import com.platform.server.crm.customer.po.CrmCustomerFollow;
import com.platform.server.crm.customer.service.FollowService;
import com.platform.server.crm.customer.vo.FollowVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final CustomerMapper customerMapper;
    private final CRMConverter crmConverter;

    @Override
    public List<FollowVO> getFollowsByCustomerId(Long customerId) {
        return followMapper.selectByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Long saveFollow(FollowSaveDTO dto) {
        // 验证客户存在
        CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }

        CrmCustomerFollow follow = new CrmCustomerFollow();
        follow.setTenantId(UserContextHolder.getTenantId());
        follow.setCustomerId(dto.getCustomerId());
        follow.setFollowType(dto.getFollowType());
        follow.setFollowContent(dto.getFollowContent());
        follow.setNextFollowTime(dto.getNextFollowTime());
        follow.setOwnerId(UserContextHolder.getUserId());

        followMapper.insert(follow);

        // 更新客户的下次跟进时间
        if (dto.getNextFollowTime() != null) {
            customer.setNextFollowTime(dto.getNextFollowTime());
            customerMapper.updateById(customer);
        }

        return follow.getId();
    }

    @Override
    public void deleteFollow(Long id) {
        followMapper.deleteById(id);
    }
}