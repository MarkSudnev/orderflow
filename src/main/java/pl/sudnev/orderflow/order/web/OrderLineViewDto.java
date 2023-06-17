package pl.sudnev.orderflow.order.web;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderLineViewDto {
  private UUID id;
  private String number;
  private String customer;
  private Double total;
}
