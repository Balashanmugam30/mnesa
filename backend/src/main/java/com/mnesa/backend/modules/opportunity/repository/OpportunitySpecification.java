package com.mnesa.backend.modules.opportunity.repository;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.domain.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpportunitySpecification {

    public static Specification<Opportunity> filter(
            UUID userId,
            String query,
            String category,
            String status,
            String priority,
            Instant deadlineBefore,
            Instant deadlineAfter,
            String organization,
            String location,
            String tag,
            boolean includeArchived) {

        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Strict user isolation
            predicates.add(cb.equal(root.get("userId"), userId));

            // 2. Default: exclude ARCHIVED unless explicitly requested
            if (!includeArchived && (status == null || !"ARCHIVED".equalsIgnoreCase(status))) {
                predicates.add(cb.notEqual(root.get("status"), OpportunityStatus.ARCHIVED));
            }

            // 3. Keyword search (case-insensitive match across title, organization, description)
            if (query != null && !query.isBlank()) {
                String searchPattern = "%" + query.trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate orgMatch = cb.like(cb.lower(root.get("organization")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(titleMatch, orgMatch, descMatch));
            }

            // 4. Category / OpportunityType
            if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
                try {
                    OpportunityType oppType = OpportunityType.valueOf(category.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("opportunityType"), oppType));
                } catch (IllegalArgumentException ignored) {}
            }

            // 5. Status
            if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                try {
                    OpportunityStatus oppStatus = OpportunityStatus.valueOf(status.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), oppStatus));
                } catch (IllegalArgumentException ignored) {}
            }

            // 6. Priority
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("priority")), priority.trim().toUpperCase()));
            }

            // 7. Deadline range
            if (deadlineBefore != null) {
                predicates.add(cb.and(
                        cb.isNotNull(root.get("deadlineAt")),
                        cb.lessThanOrEqualTo(root.get("deadlineAt"), deadlineBefore)
                ));
            }
            if (deadlineAfter != null) {
                predicates.add(cb.and(
                        cb.isNotNull(root.get("deadlineAt")),
                        cb.greaterThanOrEqualTo(root.get("deadlineAt"), deadlineAfter)
                ));
            }

            // 8. Organization
            if (organization != null && !organization.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("organization")), "%" + organization.trim().toLowerCase() + "%"));
            }

            // 9. Location
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase() + "%"));
            }

            // 10. Tag filter
            if (tag != null && !tag.isBlank()) {
                Join<Opportunity, Tag> tagJoin = root.join("tags");
                predicates.add(cb.equal(cb.lower(tagJoin.get("name")), tag.trim().toLowerCase()));
            }

            criteriaQuery.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
