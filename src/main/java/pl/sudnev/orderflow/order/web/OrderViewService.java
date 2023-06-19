package pl.sudnev.orderflow.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.OrderDetailsDto;
import pl.sudnev.orderflow.order.OrderDto;
import pl.sudnev.orderflow.order.commands.AddProductCommand;
import pl.sudnev.orderflow.order.commands.AddProductCommandHandler;
import pl.sudnev.orderflow.order.commands.CancelOrderCommand;
import pl.sudnev.orderflow.order.commands.CancelOrderCommandHandler;
import pl.sudnev.orderflow.order.commands.CreateOrderCommand;
import pl.sudnev.orderflow.order.commands.CreateOrderCommandHandler;
import pl.sudnev.orderflow.order.commands.RemoveProductCommand;
import pl.sudnev.orderflow.order.commands.RemoveProductCommandHandler;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotService;
import pl.sudnev.orderflow.shared.GridDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderViewService {

  private final OrderSnapshotService orderSnapshotService;
  private final CreateOrderCommandHandler createOrderCommandHandler;
  private final AddProductCommandHandler addProductCommandHandler;
  private final RemoveProductCommandHandler removeProductCommandHandler;
  private final CancelOrderCommandHandler cancelOrderCommandHandler;

  public GridDto<OrderLineViewDto> getOrders(final Pageable pageable) {
    final var orderPage = orderSnapshotService.getOrders(pageable);
    return GridDto.<OrderLineViewDto>builder()
        .items(orderPage.map(this::toOrderLineViewDto).getContent())
        .page(orderPage.getNumber())
        .total(orderPage.getTotalElements())
        .build();
  }

  public OrderDetailsViewDto getOrder(final UUID orderId) {
    final var order = orderSnapshotService.getOrderDetails(orderId);
    return toOrderDetailsViewDto(order);
  }

  public void createOrder(final CreateOrderViewDto dto) {
    createOrderCommandHandler.handle(
        new CreateOrderCommand(UUID.randomUUID(), dto.getCustomer())
    );
  }

  public void addProduct(final UUID orderId, final OrderProductViewDto dto) {
    addProductCommandHandler.handle(
        new AddProductCommand(orderId, dto.getProductCode(), dto.getQuantity())
    );
  }

  public void removeProduct(final UUID orderId, final String productCode, final Integer qty) {
    removeProductCommandHandler.handle(
        new RemoveProductCommand(orderId, productCode, qty)
    );
  }

  public void cancelOrder(final UUID orderId) {
    cancelOrderCommandHandler.handle(
        new CancelOrderCommand(orderId)
    );
  }

  private OrderLineViewDto toOrderLineViewDto(final OrderDto dto) {
    return OrderLineViewDto.builder()
        .id(dto.getId())
        .number(dto.getNumber())
        .customer(dto.getCustomer())
        .total(dto.getTotal())
        .state(dto.getState().name())
        .build();
  }

  private OrderDetailsViewDto toOrderDetailsViewDto(final OrderDetailsDto dto) {
    final var products = dto.getProducts().stream()
        .map(prod -> new OrderDetailsViewDto.ProductLineView(
            prod.getCode(), prod.getName(), prod.getPrice(), prod.getQuantity(), prod.getSubtotal())
        ).toList();
    return OrderDetailsViewDto.builder()
        .id(dto.getId())
        .number(dto.getNumber())
        .customer(dto.getCustomer())
        .products(products)
        .payed(dto.getPayed())
        .total(dto.getTotal())
        .status(dto.getState().name())
        .build();
  }
}
