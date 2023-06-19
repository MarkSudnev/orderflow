package pl.sudnev.orderflow.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sudnev.orderflow.product.ProductService;

import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

  private final OrderViewService orderViewService;
  private final ProductService productService;

  @GetMapping
  public String viewOrders(@PageableDefault final Pageable pageable, final Model model) {
    final var orderPage = orderViewService.getOrders(pageable);
    model.addAttribute("orderPage", orderPage);
    return "orders/orders";
  }

  @GetMapping("/{orderId}")
  public String viewOrder(@PathVariable final UUID orderId, final Model model) {
    final var order = orderViewService.getOrder(orderId);
    model.addAttribute("order", order);
    return "orders/order-details";
  }

  @GetMapping("/create")
  public String orderForm(final Model model) {
    model.addAttribute("order", new CreateOrderViewDto());
    return "orders/create-order";
  }

  @PostMapping("/create")
  public String createOrder(final CreateOrderViewDto dto) {
    orderViewService.createOrder(dto);
    return "redirect:/orders";
  }

  @GetMapping("/{orderId}/products/add")
  public String productForm(@PathVariable final UUID orderId, final Model model) {
    model.addAttribute("orderId", orderId);
    model.addAttribute("product", new OrderProductViewDto());
    model.addAttribute("products", productService.getAll());
    return "orders/update-product";
  }

  @PostMapping("/{orderId}/products/add")
  public String addProduct(@PathVariable final UUID orderId, final OrderProductViewDto dto) {
    orderViewService.addProduct(orderId, dto);
    return "redirect:/orders/" + orderId;
  }

  @GetMapping("/{orderId}/products/{productCode}/remove/{qty}")
  public String removeProduct(
      @PathVariable final UUID orderId,
      @PathVariable final String productCode,
      @PathVariable final Integer qty
  ) {
    orderViewService.removeProduct(orderId, productCode, qty);
    return "redirect:/orders/" + orderId;
  }

  @GetMapping("/{orderId}/delete")
  public String cancelOrder(@PathVariable final UUID orderId) {
    orderViewService.cancelOrder(orderId);
    return "redirect:/orders";
  }
}
