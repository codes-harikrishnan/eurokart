package com.harikrishnan.eurokart.util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService{

    @Async("taskExecutor")
    public CompletableFuture<Void> sendOrderNotification (String email, Long orderId, Map<String,String> contextMap) {
        log.info("Starting email notification");

            if(contextMap != null) {
                MDC.setContextMap(contextMap);
            }
                try {
                    log.info("Sending email notification to  {} regarding the order with id: {} " , email, orderId);
                    Thread.sleep(3000);
                    log.info("An email has been sent to {} regarding the order",email);
                    return CompletableFuture.completedFuture(null);
                }
                catch (Exception exception) {
                    log.error("Unable to send email to {} Technical error: {}" , email, exception.getMessage());
                    return CompletableFuture.failedFuture(exception);
                }
                finally {
                    MDC.clear();
                }

    }
}
