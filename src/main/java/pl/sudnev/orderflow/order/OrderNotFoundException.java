package pl.sudnev.orderflow.order;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(final String message) {
    super(message);
  }
}
