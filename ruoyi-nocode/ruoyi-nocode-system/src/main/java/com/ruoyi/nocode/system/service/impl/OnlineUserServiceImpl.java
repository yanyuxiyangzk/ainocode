package com.ruoyi.nocode.system.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.nocode.system.domain.OnlineUser;
import com.ruoyi.nocode.system.service.IOnlineUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 在线用户Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements IOnlineUserService {

    @Override
    public List<OnlineUser> list() {
        List<OnlineUser> onlineUsers = new ArrayList<>();

        // sa-token 1.37.0 API changed - return empty list for now
        // The searchTokenValueByIndex method was removed
        log.debug("Online user list not available - sa-token API changed");

        return onlineUsers;
    }

    @Override
    public OnlineUser getByToken(String tokenId) {
        try {
            Object loginId = StpUtil.getLoginIdByToken(tokenId);
            if (loginId != null) {
                OnlineUser onlineUser = new OnlineUser();
                onlineUser.setTokenId(tokenId);
                onlineUser.setUserName(String.valueOf(loginId));
                onlineUser.setLoginTime(LocalDateTime.now());
                onlineUser.setTimeout(StpUtil.getTokenTimeout());
                return onlineUser;
            }
        } catch (Exception e) {
            log.debug("获取用户信息失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean kickout(String tokenId) {
        try {
            StpUtil.kickoutByTokenValue(tokenId);
            log.info("强制下线用户 token: {}", tokenId);
            return true;
        } catch (Exception e) {
            log.error("强制下线失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int batchKickout(String[] tokenIds) {
        int count = 0;
        for (String tokenId : tokenIds) {
            if (kickout(tokenId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isOnline(String tokenId) {
        try {
            Object loginId = StpUtil.getLoginIdByToken(tokenId);
            return loginId != null;
        } catch (Exception e) {
            return false;
        }
    }
}
