---
name: mnesa-postgresql
description: MNESA PostgreSQL engineering rules covering Flyway migrations, schema normalization, indexing strategies, constraints, and query performance.
---

# MNESA PostgreSQL Data Engineering

## 1. Schema Discipline
- **Single Source of Truth:** PostgreSQL is the primary transactional store for all users, opportunities, extractions, and reminders.
- **Strict Migrations:** All schema changes must be applied via incremental Flyway migrations (`V1__...sql`, `V2__...sql`). Never modify existing migrations once committed.
- **No Destructive Shortcuts:** Renames and drops must follow multi-phase expand/contract patterns to avoid breaking running instances.

## 2. Table Design & Constraints
- **Primary Keys:** Use `UUIDv7` or `BIGSERIAL` with immutable primary key semantics.
- **Referential Integrity:** Enforce foreign key constraints with appropriate `ON DELETE RESTRICT` or `ON DELETE CASCADE` rules.
- **Check Constraints:** Use database-level `CHECK` constraints for enumerated opportunity statuses (`CAPTURED`, `EXTRACTED`, `SAVED`, `ACTED`, `COMPLETED`), deadline dates, and confidence score ranges (`0.00` to `1.00`).
- **Audit Columns:** Every table must include `created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()`.

## 3. Indexing & Performance
- **Foreign Key Indexing:** Always index foreign key columns to prevent table locks during cascades and optimize joins.
- **Compound Indexes:** Index common filter patterns (e.g., `(user_id, status)`, `(user_id, deadline_at)`).
- **Full-Text Search:** Utilize PostgreSQL `tsvector` and `GIN` indexes for fast opportunity title and description search rather than expensive `LIKE '%...%'` queries.
- **Explain Analyze:** Review query execution plans for sequential scans on tables anticipated to exceed 10,000 rows.
