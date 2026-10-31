package com.platform.server.system.log.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.server.system.log.login.dto.LoginLogQueryDTO;
import com.platform.server.system.log.login.po.SysLoginLog;
import com.platform.server.system.log.login.vo.LoginLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 多表关联查询：登录日志列表（关联用户表获取用户名）
     */
    IPage<LoginLogVO> selectLogPage(Page<LoginLogVO> page, @Param("query") LoginLogQueryDTO query);


}