package pl.sudnev.orderflow.product;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductRepository {

  private final List<Product> storage = new ArrayList<>();

  @PostConstruct
  public void init() {
    storage.add(new Product("ABC-1000", "Glasses", 1.25));
    storage.add(new Product("ABC-1001", "Jeans", 15.30));
    storage.add(new Product("ABC-1002", "Jacket", 20.50));
    storage.add(new Product("ABC-1003", "Shoes", 10.90));
  }

  public Optional<Product> findProduct(final String code) {
    return storage.stream()
        .filter(p -> p.getCode().equals(code))
        .findFirst();
  }

  public List<Product> getAll() {
    return storage;
  }
}
