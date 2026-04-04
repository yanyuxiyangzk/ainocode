package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysDictType;
import com.ruoyi.nocode.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型Controller
 */
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
@Tag(name = "字典类型管理", description = "字典类型管理接口")
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;

    /**
     * 查询字典类型列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典类型列表")
    public TableDataInfo list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return TableDataInfo.success(list);
    }

    /**
     * 获取字典类型详情
     */
    @GetMapping("/{dictId}")
    @Operation(summary = "获取字典类型详情")
    public AjaxResult getInfo(@PathVariable Long dictId) {
        SysDictType dictType = dictTypeService.selectDictTypeById(dictId);
        return dictType != null ? AjaxResult.success(dictType) : AjaxResult.error("字典类型不存在");
    }

    /**
     * 根据字典类型查询
     */
    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型查询")
    public AjaxResult getByType(@PathVariable String dictType) {
        SysDictType dictTypeObj = dictTypeService.selectDictTypeByType(dictType);
        return dictTypeObj != null ? AjaxResult.success(dictTypeObj) : AjaxResult.error("字典类型不存在");
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    @Operation(summary = "新增字典类型")
    public AjaxResult add(@RequestBody SysDictType dictType) {
        boolean success = dictTypeService.insertDictType(dictType);
        return success ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    @Operation(summary = "修改字典类型")
    public AjaxResult edit(@RequestBody SysDictType dictType) {
        boolean success = dictTypeService.updateDictType(dictType);
        return success ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    @Operation(summary = "删除字典类型")
    public AjaxResult remove(@PathVariable Long[] dictIds) {
        boolean success = dictTypeService.deleteDictTypeByIds(dictIds);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
