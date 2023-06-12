package pl.sudnev.orderflow.order.events;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.sudnev.orderflow.order.OrderEventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document
public class OrderEvent {

  @Id
  private UUID id;
  private UUID orderId;
  private LocalDateTime timestamp;
  private OrderEventType type;
  private Object payload;

}
