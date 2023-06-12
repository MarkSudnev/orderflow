package pl.sudnev.orderflow.order.snapshot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSnapshotListener {

  private final OrderSnapshotService orderSnapshotService;

  @EventListener
  public void handle(final OrderSnapshotEvent event) {
    final var snapshot = OrderSnapshot.builder()
        .orderId(event.getOrderId())
        .number(event.getNumber())
        .customer(event.getCustomer())
        .products(event.getProducts())
        .total(event.getTotal())
        .state(event.getState())
        .timestamp(event.getTimestamp())
        .build();
    orderSnapshotService.saveSnapshot(snapshot);
  }
}
