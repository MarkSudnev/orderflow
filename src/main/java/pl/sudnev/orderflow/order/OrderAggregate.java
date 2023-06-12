package pl.sudnev.orderflow.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import pl.sudnev.orderflow.order.events.OrderCancelledEvent;
import pl.sudnev.orderflow.order.events.OrderCreatedEvent;
import pl.sudnev.orderflow.order.events.OrderIssuedEvent;
import pl.sudnev.orderflow.order.events.PaymentReceivedEvent;
import pl.sudnev.orderflow.order.events.ProductAddedEvent;
import pl.sudnev.orderflow.order.events.ProductRemovedEvent;
import pl.sudnev.orderflow.shared.OrderValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static pl.sudnev.orderflow.order.OrderState.CANCELLED;
import static pl.sudnev.orderflow.order.OrderState.ISSUED;
import static pl.sudnev.orderflow.order.OrderState.NEW;
import static pl.sudnev.orderflow.order.OrderState.PAYED;

@Getter
@RequiredArgsConstructor
public class OrderAggregate {

  private final UUID id;
  private String number;
  private String customer;
  private Map<String, ProductLine> products;
  private List<Payment> payments;
  private Double total;
  private OrderState state;

  public void apply(final OrderCreatedEvent event) {
    this.number = event.getNumber();
    this.customer = event.getCustomer();
    this.state = NEW;
    this.total = 0.0;
    this.products = new HashMap<>();
    this.payments = new ArrayList<>();
  }

  public void apply(final ProductAddedEvent event) {
    final var productLine = products.getOrDefault(
        event.getProduct().getCode(),
        ProductLine.builder()
            .code(event.getProduct().getCode())
            .name(event.getProduct().getName())
            .price(event.getProduct().getPrice())
            .quantity(0)
            .subtotal(0D)
            .build()
    );
    productLine.setQuantity(productLine.getQuantity() + event.getQuantity());
    productLine.setSubtotal(productLine.getPrice() * productLine.getQuantity());
    products.put(productLine.getCode(), productLine);
    total = products.values().stream().mapToDouble(ProductLine::getSubtotal).sum();
  }

  public void apply(final ProductRemovedEvent event) {
    if (event.getQuantity() <= 0) {
      throw new OrderValidationException("Quantity must be positive");
    }
    if (!products.containsKey(event.getProduct().getCode())) {
      throw new OrderValidationException("Product is not exists in order");
    }
    final var productLine = products.get(event.getProduct().getCode());
    if (productLine.getQuantity() < event.getQuantity()) {
      throw new OrderValidationException("Unable to remove quantity more than you have");
    }
    if (productLine.getQuantity() > event.getQuantity()) {
      productLine.setQuantity(productLine.getQuantity() - event.getQuantity());
      productLine.setSubtotal(productLine.getPrice() * productLine.getQuantity());
      products.put(productLine.getCode(), productLine);
    }
    else {
      products.remove(event.getProduct().getCode());
    }
    total = products.values().stream().mapToDouble(ProductLine::getSubtotal).sum();
  }

  public void apply(final OrderCancelledEvent event) {
    if (state == CANCELLED) {
      throw new OrderValidationException("Order already cancelled");
    }
    state = CANCELLED;
  }

  public void apply(final OrderIssuedEvent event) {
    if (state == ISSUED) {
      throw new OrderValidationException("Order must be in NEW status");
    }
    state = ISSUED;
  }

  public void apply(final PaymentReceivedEvent event) {
    if (state != ISSUED) {
      throw new OrderValidationException("Order must be in ISSUED status");
    }
    payments.add(new Payment(event.getAmount(), LocalDateTime.now()));
    if (payments.stream().mapToDouble(Payment::getAmount).sum() == total) {
      state = PAYED;
    }
  }

  @Getter
  @Setter
  @Builder
  @EqualsAndHashCode
  public static class ProductLine {
    private String code;
    private String name;
    private Double price;
    private Integer quantity;
    private Double subtotal;
  }

  @Value
  @EqualsAndHashCode
  public static class Payment {
    Double amount;
    LocalDateTime timestamp;
  }

}
