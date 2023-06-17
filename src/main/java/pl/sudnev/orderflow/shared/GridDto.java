package pl.sudnev.orderflow.shared;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GridDto<T> {
  List<T> items;
  Integer page;
  Long total;
}
