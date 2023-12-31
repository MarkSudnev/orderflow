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
import pl.sudnev.orderflow.order.commands.AddProductCommandHandler;
import pl.sudnev.orderflow.order.commands.CancelOrderCommand;
import pl.sudnev.orderflow.order.commands.CancelOrderCommandHandler;
import pl.sudnev.orderflow.order.commands.CreateOrderCommand;
import pl.sudnev.orderflow.order.commands.CreateOrderCommandHandler;
import pl.sudnev.orderflow.order.commands.IssueOrderCommand;
import pl.sudnev.orderflow.order.commands.IssueOrderCommandHandler;
import pl.sudnev.orderflow.order.commands.MakePaymentCommand;
import pl.sudnev.orderflow.order.commands.MakePaymentCommandHandler;
import pl.sudnev.orderflow.order.commands.RemoveProductCommand;
import pl.sudnev.orderflow.order.commands.RemoveProductCommandHandler;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final AddProductCommandHandler addProductCommandHandler;
  private final RemoveProductCommandHandler removeProductCommandHandler;
  private final CreateOrderCommandHandler createOrderCommandHandler;
  private final IssueOrderCommandHandler issueOrderCommandHandler;
  private final CancelOrderCommandHandler cancelOrderCommandHandler;
  private final MakePaymentCommandHandler makePaymentCommandHandler;
  private final OrderSnapshotService orderSnapshotService;

  @GetMapping("/{orderId}")
  public OrderDetailsDto getOrder(@PathVariable final UUID orderId) {
    return orderSnapshotService.getOrderDetails(orderId);
  }

  @PostMapping
  public UUID createOrder(@RequestBody final CreateOrderDto dto) {
    final var orderId = UUID.randomUUID();
    createOrderCommandHandler.handle(
        new CreateOrderCommand(orderId, dto.getCustomer())
    );
    return orderId;
  }

  @PatchMapping("/{orderId}/issue")
  public void issueOrder(@PathVariable final UUID orderId) {
    issueOrderCommandHandler.handle(new IssueOrderCommand(orderId));
  }

  @PatchMapping("/{orderId}/cancel")
  public void cancelOrder(@PathVariable final UUID orderId) {
    cancelOrderCommandHandler.handle(new CancelOrderCommand(orderId));
  }

  @PatchMapping("/{orderId}/payment")
  public void makePayment(
      @PathVariable final UUID orderId,
      @RequestBody final OrderPaymentDto dto
  ) {
    makePaymentCommandHandler.handle(
        new MakePaymentCommand(orderId, dto.getAmount())
    );
  }

  @PatchMapping("/{orderId}/products/add")
  public void addProduct(
      @PathVariable final UUID orderId,
      @RequestBody final AddProductDto dto
  ) {
    addProductCommandHandler.handle(
        new AddProductCommand(orderId, dto.getProductCode(), dto.getQuantity())
    );
  }

  @PatchMapping("/{orderId}/products/remove")
  public void removeProduct(
      @PathVariable final UUID orderId,
      @RequestBody final RemoveProductDto dto
  ) {
    removeProductCommandHandler.handle(
        new RemoveProductCommand(orderId, dto.productCode, dto.getQuantity())
    );
  }
}
