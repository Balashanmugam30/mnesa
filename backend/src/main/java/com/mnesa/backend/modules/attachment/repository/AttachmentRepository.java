package com.mnesa.backend.modules.attachment.repository;

import com.mnesa.backend.modules.attachment.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findAllByUserId(UUID userId);

    List<Attachment> findAllByOpportunityIdAndUserId(UUID opportunityId, UUID userId);

    Optional<Attachment> findByCaptureIdAndUserId(UUID captureId, UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Attachment a WHERE a.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
