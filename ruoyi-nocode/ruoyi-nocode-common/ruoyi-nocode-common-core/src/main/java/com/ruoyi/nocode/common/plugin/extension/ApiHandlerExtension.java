package com.ruoyi.nocode.common.plugin.extension;

import java.util.Map;

/**
 * API 处理扩展点
 * 
 * 用于扩展 REST API 接口
 * 插件可以通过此扩展点注册自定义 API 接口
 * 
 * @author ruoyi-nocode
 */
public interface ApiHandlerExtension extends PluginExtensionPoint {

    /**
     * 获取 API 路径前缀
     *
     * @return 路径前缀，如 "/plugin/demo"
     */
    String getPathPrefix();

    /**
     * 处理 GET 请求
     *
     * @param path      请求路径
     * @param params    请求参数
     * @param headers   请求头
     * @return 响应结果
     */
    default Object handleGet(String path, Map<String, String> params, Map<String, String> headers) {
        return createNotImplementedResponse();
    }

    /**
     * 处理 POST 请求
     *
     * @param path      请求路径
     * @param body      请求体
     * @param headers   请求头
     * @return 响应结果
     */
    default Object handlePost(String path, Object body, Map<String, String> headers) {
        return createNotImplementedResponse();
    }

    /**
     * 处理 PUT 请求
     *
     * @param path      请求路径
     * @param body      请求体
     * @param headers   请求头
     * @return 响应结果
     */
    default Object handlePut(String path, Object body, Map<String, String> headers) {
        return createNotImplementedResponse();
    }

    /**
     * 处理 DELETE 请求
     *
     * @param path      请求路径
     * @param params    请求参数
     * @param headers   请求头
     * @return 响应结果
     */
    default Object handleDelete(String path, Map<String, String> params, Map<String, String> headers) {
        return createNotImplementedResponse();
    }

    /**
     * 创建未实现响应
     */
    default Map<String, Object> createNotImplementedResponse() {
        return Map.of(
                "code", 501,
                "msg", "Method Not Implemented",
                "data", (Object) null
        );
    }

    /**
     * 创建成功响应
     */
    default Map<String, Object> createSuccessResponse(Object data) {
        return Map.of(
                "code", 200,
                "msg", "success",
                "data", data
        );
    }

    /**
     * 创建错误响应
     */
    default Map<String, Object> createErrorResponse(String message) {
        return Map.of(
                "code", 500,
                "msg", message,
                "data", (Object) null
        );
    }
}
