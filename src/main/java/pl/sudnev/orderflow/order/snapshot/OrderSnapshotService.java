package pl.sudnev.orderflow.order.snapshot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sudnev.orderflow.order.OrderDetailsDto;
import pl.sudnev.orderflow.shared.OrderNotFoundException;

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

}
