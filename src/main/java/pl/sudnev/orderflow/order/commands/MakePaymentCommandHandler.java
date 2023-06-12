package pl.sudnev.orderflow.order.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sudnev.orderflow.order.OrderService;
import pl.sudnev.orderflow.order.events.PaymentReceivedEvent;

import static pl.sudnev.orderflow.order.OrderEventType.PAYMENT_RECEIVED_EVENT;

@Component
@RequiredArgsConstructor
public class MakePaymentCommandHandler {

  private final OrderService orderService;

  public void handle(final MakePaymentCommand command) {
    final var aggregate = orderService.loadAggregate(command.getOrderId());
    final var event = new PaymentReceivedEvent(command.getAmount());
    aggregate.apply(event);
    orderService.saveEvent(
        command.getOrderId(), PAYMENT_RECEIVED_EVENT, event
    );
    orderService.publishSnapshotEvent(aggregate);
  }
}
