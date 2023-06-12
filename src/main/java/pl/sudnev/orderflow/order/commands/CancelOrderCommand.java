package pl.sudnev.orderflow.order.commands;

import lombok.Value;

import java.util.UUID;

@Value
public class CancelOrderCommand {
  UUID orderId;
}
