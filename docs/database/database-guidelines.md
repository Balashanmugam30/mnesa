# MNESA Database Architecture & Flyway Guidelines

## 1. Primary Relational Store
- **Engine:** PostgreSQL 16
- **Single Source of Truth:** All user accounts, capture events, parsed opportunity data, and reminder jobs are stored transactionally in PostgreSQL.

## 2. Migration Discipline
1. **Tool:** Flyway
2. **Directory:** `backend/src/main/resources/db/migration/`
3. **Naming Scheme:** `V<Version>__<Description>.sql` (e.g., `V1__init_schema.sql`, `V2__create_opportunities_table.sql`).
4. **Immutability:** Once a migration has been committed to `main` and executed, it must NEVER be modified. New changes require a subsequent versioned script.

## 3. Schema Conventions
- **Identifiers:** Primary keys use `UUID` (specifically time-sortable UUIDv7 or `gen_random_uuid()` fallback).
- **Naming:** `snake_case` for all table names and column names.
- **Audit Fields:** Every table must include:
  ```sql
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
  ```
- **Constraints:** Use `CHECK` constraints for enumerations to guarantee database integrity independently of application code.
- **Foreign Keys:** Explicit index on all foreign key columns.
