package pl.sudnev.orderflow.order.snapshot;

import lombok.experimental.UtilityClass;
import pl.sudnev.orderflow.order.OrderAggregate;
import pl.sudnev.orderflow.order.OrderDetailsDto;

import static java.util.Optional.ofNullable;

@UtilityClass
public class OrderSnapshotMapper {

  public static OrderDetailsDto toOrderDetailsDto(final OrderSnapshot snapshot) {
    final var orderDetailsBuilder = OrderDetailsDto.builder()
        .id(snapshot.getOrderId())
        .number(snapshot.getNumber())
        .customer(snapshot.getCustomer())
        .total(snapshot.getTotal())
        .state(snapshot.getState());
    ofNullable(snapshot.getProducts())
        .map(prods -> prods.values().stream()
            .map(OrderSnapshotMapper::toProductLine)
            .toList())
        .ifPresent(orderDetailsBuilder::products);
    ofNullable(snapshot.getPayments())
        .map(pays -> pays.stream()
            .map(OrderSnapshotMapper::toPaymentLine)
            .toList())
        .ifPresent(pays -> {
          orderDetailsBuilder.payments(pays);
          orderDetailsBuilder.payed(pays.stream()
              .mapToDouble(OrderDetailsDto.PaymentLine::getAmount)
              .sum());
        });
    return orderDetailsBuilder.build();
  }

  public static OrderDetailsDto.ProductLine toProductLine(final OrderAggregate.ProductLine product) {
    return new OrderDetailsDto.ProductLine(
        product.getCode(),
        product.getName(),
        product.getPrice(),
        product.getQuantity(),
        product.getSubtotal()
    );
  }

  public static OrderDetailsDto.PaymentLine toPaymentLine(final OrderAggregate.Payment payment) {
    return new OrderDetailsDto.PaymentLine(payment.getAmount(), payment.getTimestamp());
  }
}
