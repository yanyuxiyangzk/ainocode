package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.SysDictType;
import com.ruoyi.nocode.system.mapper.SysDictTypeMapper;
import com.ruoyi.nocode.system.service.impl.SysDictTypeServiceImpl;
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
 * SysDictTypeService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("字典类型Service测试")
class SysDictTypeServiceTest {

    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @InjectMocks
    private SysDictTypeServiceImpl dictTypeService;

    private SysDictType testDictType;

    @BeforeEach
    void setUp() {
        testDictType = new SysDictType();
        testDictType.setDictId(1L);
        testDictType.setDictName("用户性别");
        testDictType.setDictType("sys_user_sex");
        testDictType.setStatus("0");
        testDictType.setRemark("用户性别字典");
    }

    @Test
    @DisplayName("查询字典类型列表")
    void testSelectDictTypeList() {
        List<SysDictType> mockList = Arrays.asList(testDictType);
        when(dictTypeMapper.selectList(any())).thenReturn(mockList);

        List<SysDictType> result = dictTypeService.selectDictTypeList(new SysDictType());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sys_user_sex", result.get(0).getDictType());
    }

    @Test
    @DisplayName("根据ID查询字典类型")
    void testSelectDictTypeById() {
        when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);

        SysDictType result = dictTypeService.selectDictTypeById(1L);

        assertNotNull(result);
        assertEquals("sys_user_sex", result.getDictType());
    }

    @Test
    @DisplayName("根据字典类型查询字典")
    void testSelectDictTypeByType() {
        when(dictTypeMapper.selectDictTypeByType("sys_user_sex")).thenReturn(testDictType);

        SysDictType result = dictTypeService.selectDictTypeByType("sys_user_sex");

        assertNotNull(result);
        assertEquals("用户性别", result.getDictName());
    }

    @Test
    @DisplayName("新增字典类型")
    void testInsertDictType() {
        when(dictTypeMapper.checkDictTypeUnique(any())).thenReturn(null);
        when(dictTypeMapper.insert(any(SysDictType.class))).thenReturn(1);

        boolean result = dictTypeService.insertDictType(testDictType);

        assertTrue(result);
        verify(dictTypeMapper, times(1)).insert(any(SysDictType.class));
    }

    @Test
    @DisplayName("修改字典类型")
    void testUpdateDictType() {
        when(dictTypeMapper.checkDictTypeUnique(any())).thenReturn(null);
        when(dictTypeMapper.updateById(any(SysDictType.class))).thenReturn(1);

        boolean result = dictTypeService.updateDictType(testDictType);

        assertTrue(result);
        verify(dictTypeMapper, times(1)).updateById(any(SysDictType.class));
    }

    @Test
    @DisplayName("删除字典类型")
    void testDeleteDictTypeByIds() {
        when(dictTypeMapper.deleteBatchIds(any())).thenReturn(2);

        boolean result = dictTypeService.deleteDictTypeByIds(new Long[]{1L, 2L});

        assertTrue(result);
    }

    @Test
    @DisplayName("校验字典类型是否唯一")
    void testCheckDictTypeUnique() {
        when(dictTypeMapper.checkDictTypeUnique("sys_user_sex")).thenReturn(null);

        boolean result = dictTypeService.checkDictTypeUnique("sys_user_sex");

        assertTrue(result);
    }

    @Test
    @DisplayName("校验字典类型是否唯一-已存在")
    void testCheckDictTypeUniqueExists() {
        when(dictTypeMapper.checkDictTypeUnique("sys_user_sex")).thenReturn(testDictType);

        boolean result = dictTypeService.checkDictTypeUnique("sys_user_sex");

        assertFalse(result);
    }
}
