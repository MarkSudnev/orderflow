package pl.sudnev.orderflow.order;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class OrderValidationException extends RuntimeException {

  public OrderValidationException(final String message) {
    super(message);
  }
}
