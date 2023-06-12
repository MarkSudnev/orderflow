package pl.sudnev.orderflow.order.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sudnev.orderflow.order.OrderService;
import pl.sudnev.orderflow.order.events.OrderIssuedEvent;

import static pl.sudnev.orderflow.order.OrderEventType.ORDER_ISSUED_EVENT;

@Component
@RequiredArgsConstructor
public class IssueOrderCommandHandler {

  private final OrderService orderService;

  public void handle(final IssueOrderCommand command) {
    final var aggregate = orderService.loadAggregate(command.getOrderId());
    final var event = new OrderIssuedEvent();
    aggregate.apply(event);
    orderService.saveEvent(command.getOrderId(), ORDER_ISSUED_EVENT);
    orderService.publishSnapshotEvent(aggregate);
  }
}
