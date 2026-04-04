package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysLogininfor;
import com.ruoyi.nocode.system.mapper.SysLogininforMapper;
import com.ruoyi.nocode.system.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志Service实现
 */
@Service
@RequiredArgsConstructor
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    private final SysLogininforMapper logininforMapper;

    @Override
    public SysLogininfor selectLogininforById(Long infoId) {
        return logininforMapper.selectLogininforById(infoId);
    }

    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor, LocalDateTime beginTime, LocalDateTime endTime) {
        return logininforMapper.selectLogininforList(logininfor, beginTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLogininfor(SysLogininfor logininfor) {
        return logininforMapper.insert(logininfor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLogininforById(Long infoId) {
        return logininforMapper.deleteById(infoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLogininforByIds(Long[] infoIds) {
        return logininforMapper.deleteBatchIds(List.of(infoIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
