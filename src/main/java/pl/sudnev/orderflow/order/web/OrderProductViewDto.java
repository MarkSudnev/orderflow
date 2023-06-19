package pl.sudnev.orderflow.order.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductViewDto {
  private String productCode;
  private Integer quantity;
}
