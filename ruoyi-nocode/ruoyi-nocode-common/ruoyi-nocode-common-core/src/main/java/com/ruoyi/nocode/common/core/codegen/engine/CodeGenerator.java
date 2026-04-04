package com.ruoyi.nocode.common.core.codegen.engine;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.generator.*;
import com.ruoyi.nocode.common.core.codegen.model.GeneratedFile;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器
 * <p>
 * 统一入口，生成Entity、Mapper、Service、Controller、Vue等代码
 *
 * @author ruoyi
 */
public class CodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(CodeGenerator.class);

    private final List<BaseCodeGenerator> generators = new ArrayList<>();

    public CodeGenerator() {
        // 注册所有生成器
        registerDefaultGenerators();
    }

    /**
     * 注册默认生成器
     */
    private void registerDefaultGenerators() {
        generators.add(new EntityGenerator());
        generators.add(new MapperGenerator());
        generators.add(new ServiceGenerator());
        generators.add(new ServiceImplGenerator());
        generators.add(new ControllerGenerator());
        generators.add(new VueApiGenerator());
        generators.add(new VueIndexGenerator());
    }

    /**
     * 注册生成器
     */
    public CodeGenerator register(BaseCodeGenerator generator) {
        generators.add(generator);
        return this;
    }

    /**
     * 批量生成代码
     *
     * @param tableInfo 表信息
     * @param config    配置
     * @return 生成的文件列表
     */
    public List<GeneratedFile> generate(TableInfo tableInfo, CodeGenConfig config) {
        List<GeneratedFile> results = new ArrayList<>();

        for (BaseCodeGenerator generator : generators) {
            // 根据配置决定是否生成
            if (!shouldGenerate(generator.getGeneratorType(), config)) {
                continue;
            }

            try {
                GeneratedFile file = generator.generate(tableInfo, config);
                results.add(file);
                if (file.isSuccess()) {
                    log.info("Generated: {} -> {}", generator.getGeneratorType(), file.getFilePath());
                } else {
                    log.error("Failed to generate {}: {}", generator.getGeneratorType(), file.getErrorMessage());
                }
            } catch (Exception e) {
                log.error("Generator {} failed", generator.getGeneratorType(), e);
                results.add(GeneratedFile.failure(generator.getFileName(tableInfo, config), e.getMessage()));
            }
        }

        return results;
    }

    /**
     * 生成单个类型的代码
     */
    public GeneratedFile generate(TableInfo tableInfo, CodeGenConfig config, String generatorType) {
        for (BaseCodeGenerator generator : generators) {
            if (generator.getGeneratorType().equals(generatorType)) {
                return generator.generate(tableInfo, config);
            }
        }
        return GeneratedFile.failure(generatorType, "Generator not found: " + generatorType);
    }

    /**
     * 根据表名生成代码
     */
    public List<GeneratedFile> generate(String tableName, CodeGenConfig config) {
        TableInfo tableInfo = TableContext.getTableInfo(tableName, config);
        return generate(tableInfo, config);
    }

    /**
     * 根据配置决定是否生成
     */
    private boolean shouldGenerate(String generatorType, CodeGenConfig config) {
        return switch (generatorType) {
            case "entity" -> config.isGenerateEntity();
            case "mapper", "service", "serviceImpl" -> config.isGenerateMapper() || config.isGenerateService();
            case "controller" -> config.isGenerateController();
            case "vueApi", "vueIndex" -> config.isGenerateVue();
            default -> true;
        };
    }

    /**
     * 获取所有注册生成器的类型
     */
    public List<String> getGeneratorTypes() {
        return generators.stream()
                .map(BaseCodeGenerator::getGeneratorType)
                .toList();
    }

    /**
     * 获取生成器实例
     */
    public BaseCodeGenerator getGenerator(String generatorType) {
        return generators.stream()
                .filter(g -> g.getGeneratorType().equals(generatorType))
                .findFirst()
                .orElse(null);
    }
}
