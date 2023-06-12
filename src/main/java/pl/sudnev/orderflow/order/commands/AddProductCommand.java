package pl.sudnev.orderflow.order.commands;

import lombok.Value;

import java.util.UUID;

@Value
public class AddProductCommand {
  UUID orderId;
  String productCode;
  Integer quantity;
}
