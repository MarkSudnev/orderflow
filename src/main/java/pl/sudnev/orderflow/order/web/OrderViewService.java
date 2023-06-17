package pl.sudnev.orderflow.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.OrderDetailsDto;
import pl.sudnev.orderflow.order.OrderDto;
import pl.sudnev.orderflow.order.snapshot.OrderSnapshotService;
import pl.sudnev.orderflow.shared.GridDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderViewService {

  private final OrderSnapshotService orderSnapshotService;

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

  private OrderLineViewDto toOrderLineViewDto(final OrderDto dto) {
    return OrderLineViewDto.builder()
        .id(dto.getId())
        .number(dto.getNumber())
        .customer(dto.getCustomer())
        .total(dto.getTotal())
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
