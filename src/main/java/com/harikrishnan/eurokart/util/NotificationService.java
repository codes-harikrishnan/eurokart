package com.harikrishnan.eurokart.util;
import com.harikrishnan.eurokart.order.domain.Order;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    public CompletableFuture<Void> sendOrderNotification (String email, Long orderId, Map<String,String> context);
}
