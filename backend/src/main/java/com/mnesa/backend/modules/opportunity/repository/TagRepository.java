package com.mnesa.backend.modules.opportunity.repository;

import com.mnesa.backend.modules.opportunity.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<Tag> findByUserIdAndName(UUID userId, String name);

    Optional<Tag> findByIdAndUserId(UUID id, UUID userId);
}
