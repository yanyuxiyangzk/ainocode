package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.exception.ServiceException;
import com.ruoyi.nocode.system.entity.SysDictData;
import com.ruoyi.nocode.system.mapper.SysDictDataMapper;
import com.ruoyi.nocode.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典数据Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (dictData.getDictLabel() != null) {
            wrapper.like(com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(dictData.getDictLabel()),
                    SysDictData::getDictLabel, dictData.getDictLabel());
        }
        if (dictData.getDictType() != null) {
            wrapper.eq(com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(dictData.getDictType()),
                    SysDictData::getDictType, dictData.getDictType());
        }
        if (dictData.getStatus() != null) {
            wrapper.eq(SysDictData::getStatus, dictData.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getDictSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectById(dictCode);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.eq(SysDictData::getStatus, "0");
        wrapper.orderByAsc(SysDictData::getDictSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDictData(SysDictData dictData) {
        if (!checkDictDataUnique(dictData.getDictType(), dictData.getDictValue())) {
            throw new ServiceException("字典键值已存在");
        }
        return dictDataMapper.insert(dictData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(SysDictData dictData) {
        if (!checkDictDataUnique(dictData.getDictType(), dictData.getDictValue())) {
            throw new ServiceException("字典键值已存在");
        }
        return dictDataMapper.updateById(dictData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictDataByIds(Long[] dictCodes) {
        return dictDataMapper.deleteBatchIds(Arrays.asList(dictCodes)) > 0;
    }

    @Override
    public boolean checkDictDataUnique(String dictType, String dictValue) {
        SysDictData existing = dictDataMapper.checkDictDataUnique(dictType, dictValue);
        return existing == null;
    }
}
