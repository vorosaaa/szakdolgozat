package org.ati.core.repository;

import org.ati.core.model.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
}
