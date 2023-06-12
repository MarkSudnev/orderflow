package pl.sudnev.orderflow.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public Product findByCode(final String code) {
    return productRepository
        .findProduct(code)
        .orElseThrow(() -> new ProductNotFoundException("Product [%s] not found".formatted(code)));
  }
}
