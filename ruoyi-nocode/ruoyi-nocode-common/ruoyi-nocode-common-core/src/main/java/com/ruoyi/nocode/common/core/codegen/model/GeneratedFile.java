package com.ruoyi.nocode.common.core.codegen.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成的文件信息
 *
 * @author ruoyi
 */
@Data
public class GeneratedFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件内容
     */
    private String content;

    /**
     * 文件类型：entity/mapper/service/controller/vue/api/js/java
     */
    private String fileType;

    /**
     * 相对路径（用于显示）
     */
    private String relativePath;

    /**
     * 文件大小（字节）
     */
    private long fileSize;

    /**
     * 是否成功生成
     */
    private boolean success;

    /**
     * 错误信息（如果有）
     */
    private String errorMessage;

    /**
     * 创建成功结果
     */
    public static GeneratedFile success(String fileName, String filePath, String content, String fileType) {
        GeneratedFile file = new GeneratedFile();
        file.setFileName(fileName);
        file.setFilePath(filePath);
        file.setContent(content);
        file.setFileType(fileType);
        file.setSuccess(true);
        file.setFileSize(content != null ? content.getBytes().length : 0);
        return file;
    }

    /**
     * 创建失败结果
     */
    public static GeneratedFile failure(String fileName, String errorMessage) {
        GeneratedFile file = new GeneratedFile();
        file.setFileName(fileName);
        file.setSuccess(false);
        file.setErrorMessage(errorMessage);
        return file;
    }
}
