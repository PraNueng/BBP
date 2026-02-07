package com.example.backend.schedulers;

import com.example.backend.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyNotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyNotificationScheduler.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * รันทุกวันที่ 1 ของเดือน เวลา 00:01 น.
     * Cron: วินาที นาที ชั่วโมง วันที่ เดือน วันในสัปดาห์, ("0 1 0 1 * ?", "0 * * * * ?")
     */
    @Scheduled(cron = "0 1 0 1 * ?") // 00:01 AM วันที่ 1 ของทุกเดือน
    public void checkMonthlyExpirations() {
        logger.info("Starting monthly expiration notification check...");
        try {
            notificationService.checkAndCreateMonthlyExpirationNotifications();
            logger.info("Monthly expiration notification check completed successfully");
        } catch (Exception e) {
            logger.error("Error during monthly expiration notification check", e);
        }
    }
}