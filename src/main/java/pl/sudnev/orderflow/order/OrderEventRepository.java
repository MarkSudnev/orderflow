package pl.sudnev.orderflow.order;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.sudnev.orderflow.order.events.OrderEvent;

import java.util.List;
import java.util.UUID;

public interface OrderEventRepository extends MongoRepository<OrderEvent, UUID> {

  List<OrderEvent> findByOrderId(final UUID orderId);
}
