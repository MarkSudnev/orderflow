package pl.sudnev.orderflow.order.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sudnev.orderflow.order.OrderAggregate;
import pl.sudnev.orderflow.order.OrderService;
import pl.sudnev.orderflow.order.events.OrderCreatedEvent;

import java.util.UUID;

import static pl.sudnev.orderflow.order.OrderEventType.ORDER_CREATED_EVENT;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler {

  private final OrderService orderService;

  public void handle(final CreateOrderCommand command) {
    final var aggregate = new OrderAggregate(command.getOrderId());
    final var orderNumber = generateOrderNumber(command.getOrderId());
    final var event = new OrderCreatedEvent(
        command.getOrderId(),
        orderNumber,
        command.getCustomer()
    );
    aggregate.apply(event);
    orderService.saveEvent(
        command.getOrderId(), ORDER_CREATED_EVENT, event
    );
    orderService.publishSnapshotEvent(aggregate);
  }

  private String generateOrderNumber(final UUID orderId) {
    final var stringId = orderId.toString();
    return "ORDER-%s".formatted(
        stringId.substring(stringId.length() - 7, stringId.length() - 1)
    );
  }
}
