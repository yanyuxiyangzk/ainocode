package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysDictData;
import com.ruoyi.nocode.system.service.ISysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据Controller
 */
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
@Tag(name = "字典数据管理", description = "字典数据管理接口")
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    /**
     * 查询字典数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典数据列表")
    public TableDataInfo list(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return TableDataInfo.success(list);
    }

    /**
     * 获取字典数据详情
     */
    @GetMapping("/{dictCode}")
    @Operation(summary = "获取字典数据详情")
    public AjaxResult getInfo(@PathVariable Long dictCode) {
        SysDictData dictData = dictDataService.selectDictDataById(dictCode);
        return dictData != null ? AjaxResult.success(dictData) : AjaxResult.error("字典数据不存在");
    }

    /**
     * 根据字典类型查询字典数据
     */
    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型查询字典数据")
    public AjaxResult getByType(@PathVariable String dictType) {
        List<SysDictData> list = dictDataService.selectDictDataByType(dictType);
        return AjaxResult.success(list);
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    @Operation(summary = "新增字典数据")
    public AjaxResult add(@RequestBody SysDictData dictData) {
        boolean success = dictDataService.insertDictData(dictData);
        return success ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 修改字典数据
     */
    @PutMapping
    @Operation(summary = "修改字典数据")
    public AjaxResult edit(@RequestBody SysDictData dictData) {
        boolean success = dictDataService.updateDictData(dictData);
        return success ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{dictCodes}")
    @Operation(summary = "删除字典数据")
    public AjaxResult remove(@PathVariable Long[] dictCodes) {
        boolean success = dictDataService.deleteDictDataByIds(dictCodes);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
