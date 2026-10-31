package com.platform.server.system.log.oper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.server.system.log.oper.dto.OperLogQueryDTO;
import com.platform.server.system.log.oper.po.SysOperLog;
import com.platform.server.system.log.oper.vo.OperLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 多表关联查询：登录日志列表（关联用户表获取用户名）
     */
    IPage<OperLogVO> selectLogPage(Page<OperLogVO> page, @Param("query") OperLogQueryDTO query);


}