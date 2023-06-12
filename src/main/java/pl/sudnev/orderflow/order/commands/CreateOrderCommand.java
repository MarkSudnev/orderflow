package pl.sudnev.orderflow.order.commands;

import lombok.Value;

import java.util.UUID;

@Value
public class CreateOrderCommand {
  UUID orderId;
  String customer;
}
