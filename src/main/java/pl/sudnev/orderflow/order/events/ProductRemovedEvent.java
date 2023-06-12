package pl.sudnev.orderflow.order.events;

import lombok.Value;
import pl.sudnev.orderflow.product.Product;

import java.io.Serializable;

@Value
public class ProductRemovedEvent implements Serializable {
  Product product;
  Integer quantity;
}
