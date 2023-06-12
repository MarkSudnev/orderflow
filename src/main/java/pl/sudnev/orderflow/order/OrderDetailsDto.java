package pl.sudnev.orderflow.order;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderDetailsDto {
  UUID id;
  String number;
  String customer;
  List<ProductLine> products;
  List<PaymentLine> payments;
  Double total;
  Double payed;
  OrderState state;

  @Value
  public static class ProductLine {
    String code;
    String name;
    Double price;
    Integer quantity;
    Double subtotal;
  }

  @Value
  public static class PaymentLine {
    Double amount;
    LocalDateTime timestamp;
  }
}
