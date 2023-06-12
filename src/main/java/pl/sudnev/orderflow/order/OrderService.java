package pl.sudnev.orderflow.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.commands.AddProductCommand;
import pl.sudnev.orderflow.order.commands.CancelOrderCommand;
import pl.sudnev.orderflow.order.commands.CreateOrderCommand;
import pl.sudnev.orderflow.order.commands.IssueOrderCommand;
import pl.sudnev.orderflow.order.commands.MakePaymentCommand;
import pl.sudnev.orderflow.order.commands.RemoveProductCommand;
import pl.sudnev.orderflow.order.events.OrderCancelledEvent;
import pl.sudnev.orderflow.order.events.OrderCreatedEvent;
import pl.sudnev.orderflow.order.events.OrderEvent;
import pl.sudnev.orderflow.order.events.OrderIssuedEvent;
import pl.sudnev.orderflow.order.events.PaymentReceivedEvent;
import pl.sudnev.orderflow.order.events.ProductAddedEvent;
import pl.sudnev.orderflow.order.events.ProductRemovedEvent;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotEvent;
import pl.sudnev.orderflow.product.ProductRepository;
import pl.sudnev.orderflow.shared.OrderValidationException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static pl.sudnev.orderflow.order.OrderEventType.ORDER_CANCELLED_EVENT;
import static pl.sudnev.orderflow.order.OrderEventType.ORDER_CREATED_EVENT;
import static pl.sudnev.orderflow.order.OrderEventType.ORDER_ISSUED_EVENT;
import static pl.sudnev.orderflow.order.OrderEventType.PAYMENT_RECEIVED_EVENT;
import static pl.sudnev.orderflow.order.OrderEventType.PRODUCT_ADDED_EVENT;
import static pl.sudnev.orderflow.order.OrderEventType.PRODUCT_REMOVED_EVENT;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderEventRepository orderEventRepository;
  private final ProductRepository productRepository;
  private final ApplicationEventPublisher eventPublisher;

  public UUID handle(final CreateOrderCommand command) {
    final var aggregate = new OrderAggregate(command.getOrderId());
    final var orderNumber = generateOrderNumber(command.getOrderId());
    final var event = new OrderCreatedEvent(
        command.getOrderId(),
        orderNumber,
        command.getCustomer()
    );
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), ORDER_CREATED_EVENT, event
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
    return command.getOrderId();
  }

  public void handle(final AddProductCommand command) {
    if (command.getQuantity() <= 0) {
      throw new OrderValidationException("Quantity must be positive");
    }
    final var aggregate = loadAggregate(command.getOrderId());
    final var product = productRepository
        .findProduct(command.getProductCode())
        .orElseThrow();
    final var event = new ProductAddedEvent(
        product, command.getQuantity()
    );
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), PRODUCT_ADDED_EVENT, event
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
  }

  public void handle(final RemoveProductCommand command) {
    final var aggregate = loadAggregate(command.getOrderId());
    final var product = productRepository
        .findProduct(command.getProductCode())
        .orElseThrow();
    final var event = new ProductRemovedEvent(
        product, command.getQuantity()
    );
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), PRODUCT_REMOVED_EVENT, event
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
  }

  public void handle(final CancelOrderCommand command) {
    final var aggregate = loadAggregate(command.getOrderId());
    final var event = new OrderCancelledEvent();
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), ORDER_CANCELLED_EVENT
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
  }

  public void handle(final IssueOrderCommand command) {
    final var aggregate = loadAggregate(command.getOrderId());
    final var event = new OrderIssuedEvent();
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), ORDER_ISSUED_EVENT
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
  }

  public void handle(final MakePaymentCommand command) {
    final var aggregate = loadAggregate(command.getOrderId());
    final var event = new PaymentReceivedEvent(command.getAmount());
    aggregate.apply(event);
    final var orderEvent = buildOrderEvent(
        command.getOrderId(), PAYMENT_RECEIVED_EVENT, event
    );
    orderEventRepository.save(orderEvent);
    publishSnapshotEvent(aggregate);
  }

  private OrderAggregate loadAggregate(final UUID id) {
    final var aggregate = new OrderAggregate(id);
    final var orderEvents = orderEventRepository.findByOrderId(id);
    orderEvents
        .stream()
        .sorted(comparing(OrderEvent::getTimestamp))
        .forEach(ev -> {
      if (ev.getPayload() instanceof OrderCreatedEvent event) {
        aggregate.apply(event);
      }
      if (ev.getPayload() instanceof ProductAddedEvent event) {
        aggregate.apply(event);
      }
      if (ev.getPayload() instanceof ProductRemovedEvent event) {
        aggregate.apply(event);
      }
      if (ev.getPayload() instanceof OrderCancelledEvent event) {
        aggregate.apply(event);
      }
      if (ev.getPayload() instanceof OrderIssuedEvent event) {
        aggregate.apply(event);
      }
      if (ev.getPayload() instanceof PaymentReceivedEvent event) {
        aggregate.apply(event);
      }
    });
    return aggregate;
  }

  private OrderEvent buildOrderEvent(
      final UUID orderId,
      final OrderEventType type,
      @Nullable final Serializable payload
  ) {
    return OrderEvent.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .type(type)
        .timestamp(LocalDateTime.now())
        .payload(payload)
        .build();
  }

  private OrderEvent buildOrderEvent(final UUID orderId, final OrderEventType type) {
    return OrderEvent.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .type(type)
        .timestamp(LocalDateTime.now())
        .build();
  }

  private String generateOrderNumber(final UUID orderId) {
    final var stringId = orderId.toString();
    return "ORDER-%s".formatted(
        stringId.substring(stringId.length() - 7, stringId.length() - 1)
    );
  }

  private void publishSnapshotEvent(final OrderAggregate aggregate) {
    final var snapshotEvent = OrderSnapshotEvent.builder()
        .orderId(aggregate.getId())
        .number(aggregate.getNumber())
        .customer(aggregate.getCustomer())
        .products(aggregate.getProducts())
        .payments(aggregate.getPayments())
        .total(aggregate.getTotal())
        .state(aggregate.getState())
        .timestamp(LocalDateTime.now())
        .build();
    eventPublisher.publishEvent(snapshotEvent);
  }
}
