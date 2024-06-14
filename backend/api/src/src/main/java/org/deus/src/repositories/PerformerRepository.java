package org.deus.src.repositories;

import org.deus.src.models.PerformerModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformerRepository extends JpaRepository<PerformerModel, Long> {
    boolean existsByName(String name);
}