package com.harikrishnan.eurokart.order.repository;

import com.harikrishnan.eurokart.configuration.JpaConfig;
import com.harikrishnan.eurokart.order.domain.Order;
import com.harikrishnan.eurokart.order.enums.OrderStatus;
import com.harikrishnan.eurokart.user.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.flyway.enabled=false")
@Import(JpaConfig.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAll_WithStatusFilter_ShouldReturnOnlyPendingOrders () {
        User user = User.builder()
                .email("test@gmail.com")
                .passwordHash("hash")
                .role("USER")
                .build();

        entityManager.persist(user);

        Order pendingOrder = Order.builder()
                .user(user)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100))
                .build();

        entityManager.persist(pendingOrder);
        entityManager.flush();

        Specification<Order> specification = (root, query, cb) -> cb.equal(root.get("user"), user);
        specification = specification.and((root, query, cb) -> cb.equal(root.get("orderStatus"), OrderStatus.PENDING));

        Page<Order> result = orderRepository.findAll(specification, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getOrderStatus()).isEqualTo(OrderStatus.PENDING);

    }

}
