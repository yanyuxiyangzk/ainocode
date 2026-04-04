package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.SysDictData;
import com.ruoyi.nocode.system.mapper.SysDictDataMapper;
import com.ruoyi.nocode.system.service.impl.SysDictDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysDictDataService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("字典数据Service测试")
class SysDictDataServiceTest {

    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private SysDictDataServiceImpl dictDataService;

    private SysDictData testDictData;

    @BeforeEach
    void setUp() {
        testDictData = new SysDictData();
        testDictData.setDictCode(1L);
        testDictData.setDictSort(1);
        testDictData.setDictLabel("男");
        testDictData.setDictValue("1");
        testDictData.setDictType("sys_user_sex");
        testDictData.setStatus("0");
    }

    @Test
    @DisplayName("查询字典数据列表")
    void testSelectDictDataList() {
        List<SysDictData> mockList = Arrays.asList(testDictData);
        when(dictDataMapper.selectList(any())).thenReturn(mockList);

        List<SysDictData> result = dictDataService.selectDictDataList(new SysDictData());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("男", result.get(0).getDictLabel());
    }

    @Test
    @DisplayName("根据ID查询字典数据")
    void testSelectDictDataById() {
        when(dictDataMapper.selectById(1L)).thenReturn(testDictData);

        SysDictData result = dictDataService.selectDictDataById(1L);

        assertNotNull(result);
        assertEquals("男", result.getDictLabel());
    }

    @Test
    @DisplayName("根据字典类型查询字典数据")
    void testSelectDictDataByType() {
        List<SysDictData> mockList = Arrays.asList(testDictData);
        when(dictDataMapper.selectList(any())).thenReturn(mockList);

        List<SysDictData> result = dictDataService.selectDictDataByType("sys_user_sex");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("新增字典数据")
    void testInsertDictData() {
        when(dictDataMapper.checkDictDataUnique(any(), any())).thenReturn(null);
        when(dictDataMapper.insert(any(SysDictData.class))).thenReturn(1);

        boolean result = dictDataService.insertDictData(testDictData);

        assertTrue(result);
        verify(dictDataMapper, times(1)).insert(any(SysDictData.class));
    }

    @Test
    @DisplayName("修改字典数据")
    void testUpdateDictData() {
        when(dictDataMapper.checkDictDataUnique(any(), any())).thenReturn(null);
        when(dictDataMapper.updateById(any(SysDictData.class))).thenReturn(1);

        boolean result = dictDataService.updateDictData(testDictData);

        assertTrue(result);
        verify(dictDataMapper, times(1)).updateById(any(SysDictData.class));
    }

    @Test
    @DisplayName("删除字典数据")
    void testDeleteDictDataByIds() {
        when(dictDataMapper.deleteBatchIds(any())).thenReturn(2);

        boolean result = dictDataService.deleteDictDataByIds(new Long[]{1L, 2L});

        assertTrue(result);
    }

    @Test
    @DisplayName("校验字典数据键值是否唯一")
    void testCheckDictDataUnique() {
        when(dictDataMapper.checkDictDataUnique(any(), any())).thenReturn(null);

        boolean result = dictDataService.checkDictDataUnique("sys_user_sex", "1");

        assertTrue(result);
    }

    @Test
    @DisplayName("校验字典数据键值是否唯一-已存在")
    void testCheckDictDataUniqueExists() {
        when(dictDataMapper.checkDictDataUnique("sys_user_sex", "1")).thenReturn(testDictData);

        boolean result = dictDataService.checkDictDataUnique("sys_user_sex", "1");

        assertFalse(result);
    }
}
