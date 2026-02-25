package com.nocode.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.DiagramLayoutEntity;
import com.nocode.admin.repository.DiagramLayoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ER图布局服务
 */
@Slf4j
@Service
public class DiagramLayoutService {
    
    private final DiagramLayoutRepository repository;
    private final ObjectMapper objectMapper;
    
    public DiagramLayoutService(DiagramLayoutRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * 保存布局数据
     */
    @Transactional
    public void saveLayout(String datasourceName, String schemaName, String layoutData, String viewConfig) {
        Optional<DiagramLayoutEntity> existingOpt;
        
        if (schemaName != null && !schemaName.isEmpty()) {
            existingOpt = repository.findByDatasourceNameAndSchemaName(datasourceName, schemaName);
        } else {
            existingOpt = repository.findByDatasourceNameAndSchemaNameIsNull(datasourceName);
        }
        
        DiagramLayoutEntity entity = existingOpt.orElse(new DiagramLayoutEntity());
        entity.setDatasourceName(datasourceName);
        entity.setSchemaName(schemaName);
        entity.setLayoutData(layoutData);
        entity.setViewConfig(viewConfig);
        
        repository.save(entity);
        log.info("保存ER图布局: datasource={}, schema={}", datasourceName, schemaName);
    }
    
    /**
     * 获取布局数据
     */
    public Map<String, Object> getLayout(String datasourceName, String schemaName) {
        Optional<DiagramLayoutEntity> entityOpt;
        
        if (schemaName != null && !schemaName.isEmpty()) {
            entityOpt = repository.findByDatasourceNameAndSchemaName(datasourceName, schemaName);
        } else {
            entityOpt = repository.findByDatasourceNameAndSchemaNameIsNull(datasourceName);
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if (entityOpt.isPresent()) {
            DiagramLayoutEntity entity = entityOpt.get();
            result.put("layoutData", parseJson(entity.getLayoutData()));
            result.put("viewConfig", parseJson(entity.getViewConfig()));
            result.put("updatedAt", entity.getUpdatedAt());
        }
        
        return result;
    }
    
    /**
     * 删除布局数据
     */
    @Transactional
    public void deleteLayout(String datasourceName, String schemaName) {
        if (schemaName != null && !schemaName.isEmpty()) {
            repository.findByDatasourceNameAndSchemaName(datasourceName, schemaName)
                .ifPresent(repository::delete);
        } else {
            repository.findByDatasourceNameAndSchemaNameIsNull(datasourceName)
                .ifPresent(repository::delete);
        }
        log.info("删除ER图布局: datasource={}, schema={}", datasourceName, schemaName);
    }
    
    private Object parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.warn("解析JSON失败: {}", e.getMessage());
            return null;
        }
    }
}
