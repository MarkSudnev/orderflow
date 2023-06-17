package pl.sudnev.orderflow.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

  private final OrderViewService orderViewService;

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
}
