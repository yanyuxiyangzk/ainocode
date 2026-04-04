package com.ruoyi.nocode.common.core.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 代码安全检查器
 * <p>
 * 在代码执行前进行静态分析，检查潜在的安全风险
 *
 * @author ruoyi
 */
public class CodeSecurityChecker {

    private static final Logger log = LoggerFactory.getLogger(CodeSecurityChecker.class);

    private final SecurityPolicy policy;

    public CodeSecurityChecker() {
        this(SecurityPolicy.strict());
    }

    public CodeSecurityChecker(SecurityPolicy policy) {
        this.policy = policy;
    }

    /**
     * 检查代码安全性
     *
     * @param sourceCode 源代码
     * @return 检查结果（null表示通过）
     */
    public List<String> checkSecurity(String sourceCode) {
        if (!policy.isEnabled()) {
            return null;
        }

        if (sourceCode == null || sourceCode.isEmpty()) {
            return List.of("Source code cannot be empty");
        }

        List<String> violations = new ArrayList<>();

        // 检查禁止的模式
        checkForbiddenPatterns(sourceCode, violations);

        // 检查禁止的类访问
        checkForbiddenClasses(sourceCode, violations);

        // 检查长度限制
        checkLength(sourceCode, violations);

        // 检查嵌套调用深度
        checkNestingDepth(sourceCode, violations);

        if (!violations.isEmpty()) {
            log.warn("Security check failed: {} violations found", violations.size());
        }

        return violations.isEmpty() ? null : violations;
    }

    /**
     * 检查禁止的模式
     */
    private void checkForbiddenPatterns(String sourceCode, List<String> violations) {
        for (String pattern : policy.getForbiddenPatterns()) {
            try {
                // 使用单词边界检查，避免误报
                String regex = "\\b" + pattern.replace(".", "\\.").replace("*", ".*") + "\\b";
                if (Pattern.compile(regex).matcher(sourceCode).find()) {
                    violations.add("Forbidden pattern detected: " + pattern);
                }
            } catch (Exception e) {
                // 如果正则表达式无效，使用简单字符串匹配
                if (sourceCode.contains(pattern)) {
                    violations.add("Forbidden pattern detected: " + pattern);
                }
            }
        }
    }

    /**
     * 检查禁止的类访问
     */
    private void checkForbiddenClasses(String sourceCode, List<String> violations) {
        for (String forbiddenClass : policy.getForbiddenSystemClasses()) {
            String className = forbiddenClass.replace("*", "");
            if (sourceCode.contains(className)) {
                violations.add("Access to forbidden class detected: " + forbiddenClass);
            }
        }

        // 检查是否有访问未允许的系统类
        for (String allowedPattern : policy.getAllowedSystemClasses()) {
            if ("*".equals(allowedPattern)) {
                return; // 允许所有类
            }
        }

        // 简单检查：确保没有使用未明确允许的系统类
        // 这里使用一个简化的启发式方法
    }

    /**
     * 检查代码长度
     */
    private void checkLength(String sourceCode, List<String> violations) {
        // 检查代码总长度
        if (sourceCode.length() > 50000) {
            violations.add("Source code too long (max 50000 characters)");
        }

        // 检查单行长度
        String[] lines = sourceCode.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > 500) {
                violations.add("Line " + (i + 1) + " too long (max 500 characters)");
            }
        }

        // 检查嵌套深度
        int maxDepth = 0;
        int currentDepth = 0;
        for (char c : sourceCode.toCharArray()) {
            if (c == '{') {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (c == '}') {
                currentDepth--;
            }
        }
        if (maxDepth > 20) {
            violations.add("Code nesting too deep (max 20 levels)");
        }
    }

    /**
     * 检查嵌套深度
     */
    private void checkNestingDepth(String sourceCode, List<String> violations) {
        int depth = 0;
        int maxDepth = 0;
        for (char c : sourceCode.toCharArray()) {
            if (c == '{') {
                depth++;
                maxDepth = Math.max(maxDepth, depth);
            } else if (c == '}') {
                depth--;
            }
        }
        if (maxDepth > 20) {
            violations.add("Excessive nesting depth: " + maxDepth + " (max allowed: 20)");
        }
    }

    /**
     * 快速检查（用于实时反馈）
     */
    public boolean quickCheck(String sourceCode) {
        if (!policy.isEnabled()) {
            return true;
        }

        if (sourceCode == null || sourceCode.isEmpty()) {
            return false;
        }

        // 快速模式：只检查最严重的违规
        for (String pattern : policy.getForbiddenPatterns()) {
            if (sourceCode.contains(pattern)) {
                return false;
            }
        }

        for (String forbiddenClass : policy.getForbiddenSystemClasses()) {
            if (sourceCode.contains(forbiddenClass.replace("*", ""))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取安全策略
     */
    public SecurityPolicy getPolicy() {
        return policy;
    }
}
