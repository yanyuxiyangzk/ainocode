package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysNotice;

/**
 * 通知公告Service接口
 */
public interface ISysNoticeService extends IService<SysNotice> {

    /**
     * 查询通知公告
     */
    SysNotice selectNoticeById(Long noticeId);

    /**
     * 新增通知公告
     */
    int insertNotice(SysNotice notice);

    /**
     * 修改通知公告
     */
    int updateNotice(SysNotice notice);

    /**
     * 删除通知公告
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除通知公告
     */
    int deleteNoticeByIds(Long[] noticeIds);
}
