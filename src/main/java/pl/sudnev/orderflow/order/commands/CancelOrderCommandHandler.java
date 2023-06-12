package pl.sudnev.orderflow.order.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sudnev.orderflow.order.OrderService;
import pl.sudnev.orderflow.order.events.OrderCancelledEvent;

import static pl.sudnev.orderflow.order.OrderEventType.ORDER_CANCELLED_EVENT;

@Component
@RequiredArgsConstructor
public class CancelOrderCommandHandler {

  private final OrderService orderService;

  public void handle(final CancelOrderCommand command) {
    final var aggregate = orderService.loadAggregate(command.getOrderId());
    final var event = new OrderCancelledEvent();
    aggregate.apply(event);
    orderService.saveEvent(command.getOrderId(), ORDER_CANCELLED_EVENT);
    orderService.publishSnapshotEvent(aggregate);
  }
}
