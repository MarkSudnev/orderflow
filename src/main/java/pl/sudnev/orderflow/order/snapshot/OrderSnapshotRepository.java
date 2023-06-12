package pl.sudnev.orderflow.order.snapshot;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderSnapshotRepository extends MongoRepository<OrderSnapshot, UUID> {
}
