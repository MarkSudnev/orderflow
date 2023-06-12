package pl.sudnev.orderflow.order.events;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class OrderCreatedEvent implements Serializable {
  UUID orderId;
  String number;
  String customer;
}
