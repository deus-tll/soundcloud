package org.deus.src.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import lombok.Data;

import jakarta.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;

@MappedSuperclass
@Data
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Created At", example = "03/14/2024 13:13:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Updated At", example = "03/14/2024 13:13:00")
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
