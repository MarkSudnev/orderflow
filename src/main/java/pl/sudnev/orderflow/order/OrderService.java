package pl.sudnev.orderflow.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.events.OrderCancelledEvent;
import pl.sudnev.orderflow.order.events.OrderCreatedEvent;
import pl.sudnev.orderflow.order.events.OrderEvent;
import pl.sudnev.orderflow.order.events.OrderIssuedEvent;
import pl.sudnev.orderflow.order.events.PaymentReceivedEvent;
import pl.sudnev.orderflow.order.events.ProductAddedEvent;
import pl.sudnev.orderflow.order.events.ProductRemovedEvent;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotEvent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderEventRepository orderEventRepository;
  private final ApplicationEventPublisher eventPublisher;

  public OrderAggregate loadAggregate(final UUID id) {
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

  public void saveEvent(
      final UUID orderId,
      final OrderEventType type,
      final Serializable payload
  ) {
    final var orderEvent = OrderEvent.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .type(type)
        .timestamp(LocalDateTime.now())
        .payload(payload)
        .build();
    orderEventRepository.save(orderEvent);
  }

  public void saveEvent(final UUID orderId, final OrderEventType type) {
    final var orderEvent = OrderEvent.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .type(type)
        .timestamp(LocalDateTime.now())
        .build();
    orderEventRepository.save(orderEvent);
  }

  public void publishSnapshotEvent(final OrderAggregate aggregate) {
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
