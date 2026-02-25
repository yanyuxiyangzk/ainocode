package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.DynamicCode;
import com.ruoyi.nocode.system.mapper.DynamicCodeMapper;
import com.ruoyi.nocode.system.service.IDynamicCodeService;
import com.ruoyi.nocode.system.service.ILiquorCompilerService;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.CompileResult;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.SandboxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态代码Service实现
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
public class DynamicCodeServiceImpl extends ServiceImpl<DynamicCodeMapper, DynamicCode> implements IDynamicCodeService {

    @Autowired
    private DynamicCodeMapper dynamicCodeMapper;

    @Autowired
    private ILiquorCompilerService liquorCompilerService;

    @Override
    public List<DynamicCode> selectDynamicCodeList(DynamicCode dynamicCode) {
        LambdaQueryWrapper<DynamicCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DynamicCode::getDelFlag, "0");
        
        if (dynamicCode.getCodeCode() != null && !dynamicCode.getCodeCode().isEmpty()) {
            wrapper.like(DynamicCode::getCodeCode, dynamicCode.getCodeCode());
        }
        if (dynamicCode.getCodeName() != null && !dynamicCode.getCodeName().isEmpty()) {
            wrapper.like(DynamicCode::getCodeName, dynamicCode.getCodeName());
        }
        if (dynamicCode.getClassName() != null && !dynamicCode.getClassName().isEmpty()) {
            wrapper.like(DynamicCode::getClassName, dynamicCode.getClassName());
        }
        if (dynamicCode.getStatus() != null && !dynamicCode.getStatus().isEmpty()) {
            wrapper.eq(DynamicCode::getStatus, dynamicCode.getStatus());
        }
        if (dynamicCode.getCodeType() != null && !dynamicCode.getCodeType().isEmpty()) {
            wrapper.eq(DynamicCode::getCodeType, dynamicCode.getCodeType());
        }
        if (dynamicCode.getEnabled() != null) {
            wrapper.eq(DynamicCode::getEnabled, dynamicCode.getEnabled());
        }
        
        wrapper.orderByDesc(DynamicCode::getUpdateTime);
        
        return list(wrapper);
    }

    @Override
    public DynamicCode selectByCodeCode(String codeCode) {
        return dynamicCodeMapper.selectByCodeCode(codeCode);
    }

    @Override
    public DynamicCode selectByClassName(String className) {
        return dynamicCodeMapper.selectByClassName(className);
    }

    @Override
    public List<DynamicCode> selectEnabledCodes() {
        return dynamicCodeMapper.selectEnabledCodes();
    }

    @Override
    public boolean checkCodeCodeUnique(DynamicCode dynamicCode) {
        Long codeId = dynamicCode.getCodeId();
        DynamicCode existing = dynamicCodeMapper.checkCodeCodeUnique(
                dynamicCode.getCodeCode(), codeId);
        return existing == null;
    }

    @Override
    public boolean checkClassNameUnique(DynamicCode dynamicCode) {
        Long codeId = dynamicCode.getCodeId();
        DynamicCode existing = dynamicCodeMapper.checkClassNameUnique(
                dynamicCode.getClassName(), codeId);
        return existing == null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompileResult compileCode(Long codeId) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            return CompileResult.failure("代码不存在: " + codeId);
        }
        
        return liquorCompilerService.compileAndCache(dynamicCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompileStatistics compileAllDraft() {
        List<DynamicCode> draftCodes = dynamicCodeMapper.selectByStatus(DynamicCode.STATUS_DRAFT);
        
        int total = draftCodes.size();
        int success = 0;
        int failed = 0;
        List<String> failedCodes = new ArrayList<>();
        
        for (DynamicCode code : draftCodes) {
            try {
                CompileResult result = liquorCompilerService.compileAndCache(code);
                if (result.success()) {
                    success++;
                } else {
                    failed++;
                    failedCodes.add(code.getCodeCode() + ": " + result.error());
                }
            } catch (Exception e) {
                failed++;
                failedCodes.add(code.getCodeCode() + ": " + e.getMessage());
            }
        }
        
        return CompileStatistics.of(total, success, failed, failedCodes);
    }

    @Override
    public Object executeMethod(Long codeId, String methodName, Object... args) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            throw new RuntimeException("代码不存在: " + codeId);
        }
        
        if (!Boolean.TRUE.equals(dynamicCode.getEnabled())) {
            throw new RuntimeException("代码未启用: " + dynamicCode.getCodeCode());
        }
        
        // 更新执行统计
        dynamicCodeMapper.incrementExecuteCount(codeId);
        
        // 执行方法
        return liquorCompilerService.executeStaticMethod(dynamicCode.getClassName(), methodName, args);
    }

    @Override
    public SandboxResult executeInSandbox(Long codeId, String methodName, Object... args) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            return SandboxResult.failure("代码不存在: " + codeId);
        }
        
        // 更新执行统计
        dynamicCodeMapper.incrementExecuteCount(codeId);
        
        // 在沙箱中执行
        return liquorCompilerService.executeInSandbox(dynamicCode, methodName, args);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean hotReplace(Long codeId) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            return false;
        }
        
        return liquorCompilerService.hotReplace(dynamicCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishCode(Long codeId) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            return false;
        }
        
        // 如果未编译，先编译
        if (!DynamicCode.STATUS_COMPILED.equals(dynamicCode.getStatus())) {
            CompileResult result = compileCode(codeId);
            if (!result.success()) {
                return false;
            }
            dynamicCode = getById(codeId);
        }
        
        // 发布
        dynamicCode.setStatus(DynamicCode.STATUS_PUBLISHED);
        dynamicCode.setEnabled(true);
        dynamicCode.setUpdateTime(LocalDateTime.now());
        updateById(dynamicCode);
        
        log.info("发布动态代码: {}", dynamicCode.getCodeCode());
        return true;
    }

    @Override
    public Object createInstance(Long codeId, Object... args) {
        DynamicCode dynamicCode = getById(codeId);
        if (dynamicCode == null) {
            throw new RuntimeException("代码不存在: " + codeId);
        }
        
        if (!Boolean.TRUE.equals(dynamicCode.getEnabled())) {
            throw new RuntimeException("代码未启用: " + dynamicCode.getCodeCode());
        }
        
        return liquorCompilerService.newInstance(dynamicCode.getClassName(), args);
    }
}
