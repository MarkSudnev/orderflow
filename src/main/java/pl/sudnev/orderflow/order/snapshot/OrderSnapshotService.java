package pl.sudnev.orderflow.order.snapshot;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.OrderDetailsDto;
import pl.sudnev.orderflow.order.OrderDto;
import pl.sudnev.orderflow.order.OrderNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderSnapshotService {

  private final OrderSnapshotRepository orderSnapshotRepository;

  public void saveSnapshot(final OrderSnapshot snapshot) {
    orderSnapshotRepository.save(snapshot);
  }

  public OrderDetailsDto getOrderDetails(final UUID orderId) {
    return orderSnapshotRepository.findById(orderId)
        .map(OrderSnapshotMapper::toOrderDetailsDto)
        .orElseThrow(() ->
            new OrderNotFoundException("Order [%s] not found".formatted(orderId))
        );
  }

  public Page<OrderDto> getOrders(final Pageable pageable) {
    return orderSnapshotRepository.findAll(pageable)
        .map(order ->
            new OrderDto(
                order.getOrderId(),
                order.getNumber(),
                order.getCustomer(),
                order.getTotal(),
                order.getState()
            )
        );
  }

}
