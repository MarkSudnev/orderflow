package pl.sudnev.orderflow.order.events;

import lombok.Value;
import pl.sudnev.orderflow.product.Product;

import java.io.Serializable;

@Value
public class ProductAddedEvent implements Serializable {
  Product product;
  Integer quantity;
}
