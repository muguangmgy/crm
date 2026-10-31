package com.platform.server.crm.customer.service;

import com.platform.server.crm.customer.dto.FollowSaveDTO;
import com.platform.server.crm.customer.vo.FollowVO;

import java.util.List;

public interface FollowService {

    /** 获取客户跟进记录列表 */
    List<FollowVO> getFollowsByCustomerId(Long customerId);

    /** 保存跟进记录 */
    Long saveFollow(FollowSaveDTO dto);

    /** 删除跟进记录 */
    void deleteFollow(Long id);
}