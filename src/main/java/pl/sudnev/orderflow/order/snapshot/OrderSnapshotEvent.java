package pl.sudnev.orderflow.order.snapshot;

import lombok.Builder;
import lombok.Value;
import pl.sudnev.orderflow.order.OrderAggregate;
import pl.sudnev.orderflow.order.OrderState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class OrderSnapshotEvent {
  UUID orderId;
  String number;
  String customer;
  Map<String, OrderAggregate.ProductLine> products;
  List<OrderAggregate.Payment> payments;
  Double total;
  OrderState state;
  LocalDateTime timestamp;
}
