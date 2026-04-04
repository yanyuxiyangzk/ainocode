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

        // 获取所有在线用户的token列表
        List<Object> tokenValueList = StpUtil.searchTokenValueByIndex(0, -1, 0);

        for (Object tokenValue : tokenValueList) {
            try {
                String token = (String) tokenValue;
                // 获取登录id
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId != null) {
                    OnlineUser onlineUser = new OnlineUser();
                    onlineUser.setTokenId(token);
                    onlineUser.setUserName(String.valueOf(loginId));
                    onlineUser.setLoginTime(LocalDateTime.now());
                    onlineUser.setTimeout(StpUtil.getTokenTimeout());

                    // 尝试从session获取更多信息
                    try {
                        var session = StpUtil.getSessionByToken(token);
                        if (session != null) {
                            Object nickName = session.get("nickName");
                            if (nickName != null) {
                                onlineUser.setUserName((String) nickName);
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }

                    onlineUsers.add(onlineUser);
                }
            } catch (Exception e) {
                log.debug("获取在线用户信息失败: {}", e.getMessage());
            }
        }

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

                try {
                    var session = StpUtil.getSessionByToken(tokenId);
                    if (session != null) {
                        Object nickName = session.get("nickName");
                        if (nickName != null) {
                            onlineUser.setUserName((String) nickName);
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }

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
