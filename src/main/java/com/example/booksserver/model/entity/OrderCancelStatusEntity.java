package com.example.booksserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@Accessors(chain = true)
public class OrderCancelStatusEntity {
    @Id
    @Column(name = "order_id")
    private Long id;

    @OneToOne(mappedBy = "orderCancelStatus")
    @MapsId
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CancelStatus status;

    @Column
    private LocalDateTime dateRequested;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderCancelStatusEntity that = (OrderCancelStatusEntity) o;

        if (!Objects.equals(id, that.id)) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + status.hashCode();
        return result;
    }
}
