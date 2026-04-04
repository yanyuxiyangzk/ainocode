package com.ruoyi.nocode.system.task;

import com.ruoyi.nocode.system.service.ISysLogininforService;
import com.ruoyi.nocode.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志清理定时任务
 *
 * 自动清理过期的操作日志和登录日志
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SysLogCleanupTask {

    private final ISysOperLogService operLogService;
    private final ISysLogininforService logininforService;

    /**
     * 操作日志保留天数（默认90天）
     */
    private static final int OPER_LOG_RETENTION_DAYS = 90;

    /**
     * 登录日志保留天数（默认90天）
     */
    private static final int LOGIN_LOG_RETENTION_DAYS = 90;

    /**
     * 每天凌晨2点执行清理任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOperLog() {
        log.info("Starting operation log cleanup task...");
        try {
            operLogService.cleanOperLog();
            log.info("Operation log cleanup completed");
        } catch (Exception e) {
            log.error("Failed to cleanup operation log", e);
        }
    }

    /**
     * 每天凌晨3点执行清理任务
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupLogininfor() {
        log.info("Starting login log cleanup task...");
        try {
            logininforService.cleanLogininfor();
            log.info("Login log cleanup completed");
        } catch (Exception e) {
            log.error("Failed to cleanup login log", e);
        }
    }
}
