package pl.sudnev.orderflow.order;

import lombok.Value;

import java.util.UUID;

@Value
public class OrderDto {
  UUID id;
  String number;
  String customer;
  Double total;
  OrderState state;
}
