package pl.sudnev.orderflow.order.snapshot;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.sudnev.orderflow.order.OrderAggregate;
import pl.sudnev.orderflow.order.OrderState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Document
public class OrderSnapshot {

  @Id
  private UUID orderId;
  private String number;
  private String customer;
  private Map<String, OrderAggregate.ProductLine> products;
  private List<OrderAggregate.Payment> payments;
  private Double total;
  private OrderState state;
  private LocalDateTime timestamp;
}
