package pl.sudnev.orderflow.order.events;

import lombok.Value;

import java.io.Serializable;

@Value
public class PaymentReceivedEvent implements Serializable {
  Double amount;
}
