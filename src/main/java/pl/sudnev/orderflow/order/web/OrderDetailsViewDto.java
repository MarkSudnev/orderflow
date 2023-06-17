package pl.sudnev.orderflow.order.web;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderDetailsViewDto {
  UUID id;
  String number;
  String customer;
  List<ProductLineView> products;
  Double total;
  Double payed;
  String status;

  @Value
  public static class ProductLineView {
    String code;
    String name;
    Double price;
    Integer quantity;
    Double subtotal;
  }

}
