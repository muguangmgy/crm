package com.platform.server.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.service.RedisLockService;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.common.constants.CRMConstants;
import com.platform.server.crm.common.converter.CRMConverter;
import com.platform.server.crm.common.enums.PoolTypeEnum;
import com.platform.server.crm.customer.dto.*;
import com.platform.server.crm.customer.mapper.*;
import com.platform.server.crm.customer.po.*;
import com.platform.server.crm.customer.service.*;
import com.platform.server.crm.customer.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final ContactMapper contactMapper;
    private final TagMapper tagMapper;
    private final TagRelMapper tagRelMapper;
    private final FollowMapper followMapper;
    private final CRMConverter crmConverter;
    private final RedisLockService redisLockService;

    @Override
    public PageVO<CustomerVO> getPrivatePoolPage(CustomerQueryDTO query) {
        query.setPoolType(PoolTypeEnum.PRIVATE.getCode());
        Page<CrmCustomer> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<CustomerVO> result = customerMapper.selectPrivatePoolPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageVO<CustomerVO> getPublicPoolPage(CustomerQueryDTO query) {
        query.setPoolType(PoolTypeEnum.PUBLIC.getCode());
        Page<CrmCustomer> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<CustomerVO> result = customerMapper.selectPublicPoolPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public CustomerDetailVO getCustomerDetail(Long id) {
        CustomerVO customerVO = customerMapper.selectCustomerById(id);
        if (customerVO == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }

        CustomerDetailVO detailVO = new CustomerDetailVO();
        detailVO.setCustomer(customerVO);

        // 获取联系人列表
        List<CrmCustomerContact> contacts = contactMapper.selectByCustomerId(id);
        detailVO.setContacts(crmConverter.toContactVOList(contacts));

        // 获取跟进记录
        List<FollowVO> follows = followMapper.selectByCustomerId(id);
        detailVO.setFollowRecords(follows);

        // 获取标签
        List<Long> tagIds = tagRelMapper.selectTagIdsByCustomerId(id);
        customerVO.setTagIds(tagIds);
        if (!tagIds.isEmpty()) {
            List<CrmCustomerTag> tags = tagMapper.selectByCustomerId(id);
            customerVO.setTags(crmConverter.toTagVOList(tags));
        }

        return detailVO;
    }

    @Override
    @Transactional
    public Long saveCustomer(CustomerSaveDTO dto) {
        CrmCustomer customer;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            customer = new CrmCustomer();
            customer.setTenantId(UserContextHolder.getTenantId());
            customer.setDeptId(UserContextHolder.getDeptId());
            customer.setCustomerNo(generateCustomerNo());
            customer.setPoolType(PoolTypeEnum.PRIVATE.getCode());
            customer.setOwnerId(UserContextHolder.getUserId());
            customer.setLockTime(LocalDateTime.now());
        } else {
            customer = customerMapper.selectById(dto.getId());
            if (customer == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
            }
        }

        // 设置通用字段
        customer.setCustomerType(dto.getCustomerType());
        customer.setCustomerName(dto.getCustomerName());
        customer.setIndustry(dto.getIndustry());
        customer.setCompanySize(dto.getCompanySize());
        customer.setProvince(dto.getProvince());
        customer.setCity(dto.getCity());
        customer.setAddress(dto.getAddress());
        customer.setWebsite(dto.getWebsite());
        customer.setGender(dto.getGender());
        customer.setBirthday(dto.getBirthday());
        customer.setOccupation(dto.getOccupation());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setSource(dto.getSource());
        customer.setLevel(dto.getLevel() != null ? dto.getLevel() : 1);
        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        customer.setRemark(dto.getRemark());

        if (isNew) {
            customerMapper.insert(customer);
        } else {
            customerMapper.updateById(customer);
        }

        // 处理标签
        if (dto.getTagIds() != null) {
            bindTags(customer.getId(), dto.getTagIds());
        }

        return customer.getId();
    }

    @Override
    public void deleteCustomer(Long id) {
        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }
        customerMapper.deleteById(id);
        // 删除关联的标签
        tagRelMapper.deleteByCustomerId(id);
    }

    @Override
    public void takeFromPool(Long customerId) {
        String lockKey = CRMConstants.LOCK_POOL_TAKE + customerId;
        Boolean result = redisLockService.executeWithLock(lockKey, 5, 10, () -> {
            CrmCustomer customer = customerMapper.selectById(customerId);
            if (customer == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
            }
            if (customer.getPoolType() != PoolTypeEnum.PUBLIC.getCode()) {
                throw new BusinessException("客户不在公海池中");
            }

            // 检查用户私有池数量上限
            int count = customerMapper.countByOwner(UserContextHolder.getUserId());
            int maxLimit = getMaxPerUser();
            if (count >= maxLimit) {
                throw new BusinessException("私有池客户数量已达上限(" + maxLimit + "个)");
            }

            // 更新为私有池
            customer.setPoolType(PoolTypeEnum.PRIVATE.getCode());
            customer.setOwnerId(UserContextHolder.getUserId());
            customer.setLockTime(LocalDateTime.now());
            customerMapper.updateById(customer);
            return true;
        });

        if (result == null) {
            throw new BusinessException("获取锁失败，请稍后重试");
        }
    }

    @Override
    public void releaseToPool(Long customerId) {
        CrmCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }
        if (customer.getPoolType() != PoolTypeEnum.PRIVATE.getCode()) {
            throw new BusinessException("客户不在私有池中");
        }

        // 更新为公海池
        customer.setPoolType(PoolTypeEnum.PUBLIC.getCode());
        customer.setOwnerId(null);
        customer.setLockTime(null);
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional
    public void transferCustomer(PoolTransferDTO dto) {
        CrmCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }
        if (customer.getPoolType() != PoolTypeEnum.PRIVATE.getCode()) {
            throw new BusinessException("客户不在私有池中");
        }

        // 检查新负责人上限
        int count = customerMapper.countByOwner(dto.getNewOwnerId());
        int maxLimit = getMaxPerUser();
        if (count >= maxLimit) {
            throw new BusinessException("目标用户私有池客户数量已达上限");
        }

        customer.setOwnerId(dto.getNewOwnerId());
        customer.setLockTime(LocalDateTime.now());
        customerMapper.updateById(customer);
    }

    @Override
    public List<TagVO> getCustomerTags(Long customerId) {
        List<CrmCustomerTag> tags = tagMapper.selectByCustomerId(customerId);
        return crmConverter.toTagVOList(tags);
    }

    @Override
    @Transactional
    public void bindTags(Long customerId, List<Long> tagIds) {
        // 先删除旧的关联
        tagRelMapper.deleteByCustomerId(customerId);

        // 新增关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<CrmCustomerTagRel> rels = tagIds.stream()
                    .map(tagId -> {
                        CrmCustomerTagRel rel = new CrmCustomerTagRel();
                        rel.setCustomerId(customerId);
                        rel.setTagId(tagId);
                        return rel;
                    })
                    .collect(Collectors.toList());
            tagRelMapper.batchInsert(rels);
        }
    }

    @Override
    public List<TagVO> getAllTags() {
        LambdaQueryWrapper<CrmCustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmCustomerTag::getTenantId, UserContextHolder.getTenantId());
        wrapper.eq(CrmCustomerTag::getStatus, 1);
        wrapper.orderByAsc(CrmCustomerTag::getSort);
        List<CrmCustomerTag> tags = tagMapper.selectList(wrapper);
        return crmConverter.toTagVOList(tags);
    }

    /** 生成客户编号 */
    private String generateCustomerNo() {
        return CRMConstants.CUSTOMER_NO_PREFIX + System.currentTimeMillis();
    }

    /** 获取每人最大私有客户数 */
    private int getMaxPerUser() {
        return CRMConstants.POOL_MAX_PER_USER;
    }
}