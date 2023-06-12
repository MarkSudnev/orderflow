package pl.sudnev.orderflow.order.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sudnev.orderflow.order.OrderService;
import pl.sudnev.orderflow.order.events.ProductAddedEvent;
import pl.sudnev.orderflow.product.ProductService;

import static pl.sudnev.orderflow.order.OrderEventType.PRODUCT_ADDED_EVENT;

@Component
@RequiredArgsConstructor
public class AddProductCommandHandler {

  private final OrderService orderService;
  private final ProductService productService;

  public void handle(AddProductCommand command) {
    final var aggregate = orderService.loadAggregate(command.getOrderId());
    final var product = productService.findByCode(command.getProductCode());
    final var event = new ProductAddedEvent(product, command.getQuantity());
    aggregate.apply(event);
    orderService.saveEvent(command.getOrderId(), PRODUCT_ADDED_EVENT, event);
    orderService.publishSnapshotEvent(aggregate);
  }
}
