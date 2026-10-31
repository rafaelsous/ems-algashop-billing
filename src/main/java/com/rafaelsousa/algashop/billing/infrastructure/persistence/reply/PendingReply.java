package com.rafaelsousa.algashop.billing.infrastructure.persistence.reply;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pending_reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingReply {

    @Id
    @EqualsAndHashCode.Include
    private UUID invoiceId;

    private String orderId;

    private String correlationId;

    private String replyTopic;

    private OffsetDateTime createdAt;

    public PendingReply(UUID invoiceId, String orderId, String correlationId, String replyTopic) {
        this.invoiceId = Objects.requireNonNull(invoiceId);
        this.orderId = Objects.requireNonNull(orderId);
        this.correlationId = Objects.requireNonNull(correlationId);
        this.replyTopic = Objects.requireNonNull(replyTopic);
        this.createdAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PendingReply that = (PendingReply) object;
        return Objects.equals(invoiceId, that.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(invoiceId);
    }
}
