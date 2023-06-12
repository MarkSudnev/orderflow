package pl.sudnev.orderflow.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveProductDto {
  String productCode;
  Integer quantity;
}
