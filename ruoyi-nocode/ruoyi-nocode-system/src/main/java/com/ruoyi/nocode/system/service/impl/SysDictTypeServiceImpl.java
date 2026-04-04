package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.exception.ServiceException;
import com.ruoyi.nocode.system.entity.SysDictType;
import com.ruoyi.nocode.system.mapper.SysDictTypeMapper;
import com.ruoyi.nocode.system.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典类型Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dictType.getDictName() != null) {
            wrapper.like(com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(dictType.getDictName()),
                    SysDictType::getDictName, dictType.getDictName());
        }
        if (dictType.getDictType() != null) {
            wrapper.like(com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(dictType.getDictType()),
                    SysDictType::getDictType, dictType.getDictType());
        }
        if (dictType.getStatus() != null) {
            wrapper.eq(SysDictType::getStatus, dictType.getStatus());
        }
        wrapper.orderByDesc(SysDictType::getCreateTime);
        return dictTypeMapper.selectList(wrapper);
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectById(dictId);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDictType(SysDictType dictType) {
        if (!checkDictTypeUnique(dictType.getDictType())) {
            throw new ServiceException("字典类型已存在");
        }
        return dictTypeMapper.insert(dictType) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictType dictType) {
        if (!checkDictTypeUnique(dictType.getDictType())) {
            throw new ServiceException("字典类型已存在");
        }
        return dictTypeMapper.updateById(dictType) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictTypeByIds(Long[] dictIds) {
        return dictTypeMapper.deleteBatchIds(Arrays.asList(dictIds)) > 0;
    }

    @Override
    public boolean checkDictTypeUnique(String dictType) {
        SysDictType existing = dictTypeMapper.checkDictTypeUnique(dictType);
        return existing == null;
    }
}
