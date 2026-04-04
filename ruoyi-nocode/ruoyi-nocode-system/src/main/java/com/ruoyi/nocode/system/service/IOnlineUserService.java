package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.domain.OnlineUser;

import java.util.List;

/**
 * 在线用户Service接口
 */
public interface IOnlineUserService {

    /**
     * 查询所有在线用户
     */
    List<OnlineUser> list();

    /**
     * 根据token查询在线用户
     */
    OnlineUser getByToken(String tokenId);

    /**
     * 强制下线指定用户
     */
    boolean kickout(String tokenId);

    /**
     * 批量强制下线
     */
    int batchKickout(String[] tokenIds);

    /**
     * 检查用户是否在线
     */
    boolean isOnline(String tokenId);
}
