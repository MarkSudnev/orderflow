package pl.sudnev.orderflow.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sudnev.orderflow.order.commands.AddProductCommand;
import pl.sudnev.orderflow.order.commands.CancelOrderCommand;
import pl.sudnev.orderflow.order.commands.CreateOrderCommand;
import pl.sudnev.orderflow.order.commands.IssueOrderCommand;
import pl.sudnev.orderflow.order.commands.MakePaymentCommand;
import pl.sudnev.orderflow.order.commands.RemoveProductCommand;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final OrderSnapshotService orderSnapshotService;

  @GetMapping("/{orderId}")
  public OrderDetailsDto getOrder(@PathVariable final UUID orderId) {
    return orderSnapshotService.getOrderDetails(orderId);
  }

  @PostMapping
  public UUID createOrder(@RequestBody final CreateOrderDto dto) {
    return orderService.handle(
        new CreateOrderCommand(UUID.randomUUID(), dto.getCustomer())
    );
  }

  @PatchMapping("/{orderId}/issue")
  public void issueOrder(@PathVariable final UUID orderId) {
    orderService.handle(new IssueOrderCommand(orderId));
  }

  @PatchMapping("/{orderId}/cancel")
  public void cancelOrder(@PathVariable final UUID orderId) {
    orderService.handle(new CancelOrderCommand(orderId));
  }

  @PatchMapping("/{orderId}/payment")
  public void makePayment(
      @PathVariable final UUID orderId,
      @RequestBody final OrderPaymentDto dto
  ) {
    orderService.handle(
        new MakePaymentCommand(orderId, dto.getAmount())
    );
  }

  @PatchMapping("/{orderId}/products/add")
  public void addProduct(
      @PathVariable final UUID orderId,
      @RequestBody final AddProductDto dto
  ) {
    orderService.handle(
        new AddProductCommand(orderId, dto.getProductCode(), dto.getQuantity())
    );
  }

  @PatchMapping("/{orderId}/products/remove")
  public void removeProduct(
      @PathVariable final UUID orderId,
      @RequestBody final RemoveProductDto dto
  ) {
    orderService.handle(
        new RemoveProductCommand(orderId, dto.productCode, dto.getQuantity())
    );
  }
}
