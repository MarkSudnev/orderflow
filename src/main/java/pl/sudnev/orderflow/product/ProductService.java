package pl.sudnev.orderflow.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public Product findByCode(final String code) {
    return productRepository
        .findProduct(code)
        .orElseThrow(() -> new ProductNotFoundException("Product [%s] not found".formatted(code)));
  }

  public List<Product> getAll() {
    return productRepository.getAll();
  }
}
