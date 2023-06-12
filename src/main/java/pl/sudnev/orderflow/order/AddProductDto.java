package pl.sudnev.orderflow.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductDto {
  private String productCode;
  private Integer quantity;
}
