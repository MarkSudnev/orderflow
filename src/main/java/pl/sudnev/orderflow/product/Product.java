package pl.sudnev.orderflow.product;

import lombok.Value;

@Value
public class Product {
  String code;
  String name;
  Double price;
}
