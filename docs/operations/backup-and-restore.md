# MNESA Production Backup & Disaster Recovery Runbook
**Document ID:** OPS-BKP-P10-005  
**Phase:** 10 — Final Security Audit, Quality & Production Readiness  
**Target Architecture:** PostgreSQL 16, MinIO Object Storage, Flyway Migrations  
**Target Audience:** DevOps Engineers, Database Administrators, Site Reliability Engineers  
**Status:** **OPERATIONAL STANDARD**  

---

## 1. Objectives, RPO, and RTO

This runbook defines the operational standards and procedural instructions for performing automated backups, periodic test drills, and disaster recovery for the MNESA production platform.

### Service Level Objectives

- **Recovery Point Objective (RPO):** < 15 minutes for transactional data; < 1 hour for media storage.
- **Recovery Time Objective (RTO):** < 30 minutes for complete service restoration to a clean state.
- **Data Integrity Standard:** 100% relational integrity verified via foreign key and Flyway migration validation.

---

## 2. Backup Targets & Storage Architecture

MNESA persists state in two distinct data layers:

1. **Relational Database (PostgreSQL 16):**
   - Single source of truth containing user identity, opportunities, reminders, captures, extractions, activities, and metadata.
   - Managed via versioned Flyway migrations (`V1__init_schema.sql` through `V7__screenshot_ocr_assistant_insights.sql`).
2. **Object Storage (MinIO / S3):**
   - Stores user-uploaded screenshot binaries, attachments, and OCR raw image files under bucket `mnesa-attachments`.

---

## 3. PostgreSQL Backup Procedures

### 3.1 Automated Logical Backups (`pg_dump`)
Logical backups generate consistent snapshot dumps of all tables, indexes, sequences, and migration metadata.

#### Daily Backup Command (Custom Compressed Format)
```bash
# Set secure execution variables
export PGPASSWORD="${DB_PASSWORD}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/var/backups/mnesa/postgres"
BACKUP_FILE="${BACKUP_DIR}/mnesa_db_${TIMESTAMP}.dump"

# Execute consistent atomic dump
pg_dump -h "${DB_HOST:-localhost}" \
        -p "${DB_PORT:-5432}" \
        -U "${DB_USER:-mnesa_user}" \
        -d "${DB_NAME:-mnesa}" \
        -F c \
        -b \
        -v \
        -f "${BACKUP_FILE}"

# Encrypt backup archive with GPG
gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase "${BACKUP_ENCRYPTION_KEY}" "${BACKUP_FILE}"
rm -f "${BACKUP_FILE}"
```

### 3.2 Continuous WAL Archiving & Point-in-Time Recovery (PITR)
For enterprise production instances, PostgreSQL Write-Ahead Logging (WAL) archiving must be active:

```ini
# postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'test ! -f /var/lib/postgresql/wal_archive/%f && cp %p /var/lib/postgresql/wal_archive/%f'
archive_timeout = 900
```

### 3.3 Backup Retention Matrix

| Tier | Frequency | Retention Window | Destination |
|------|-----------|------------------|-------------|
| **Hourly WAL** | Every 15 min | 7 Days | Local SSD + Offsite S3 Glacier |
| **Daily Dump** | Daily at 02:00 UTC | 30 Days | Encrypted Cloud Backup Vault |
| **Weekly Snapshot** | Weekly on Sunday | 90 Days | Multi-region Cold Storage |
| **Monthly Archive** | 1st of each month | 365 Days | Compliant Long-term Archive |

---

## 4. PostgreSQL Restoration Procedures

### 4.1 Full Restoration from Logical Dump
When restoring to a fresh or recovered PostgreSQL instance:

```bash
# Step 1: Decrypt archive
gpg --decrypt --batch --yes --passphrase "${BACKUP_ENCRYPTION_KEY}" \
    -o /tmp/mnesa_restore.dump \
    "${BACKUP_DIR}/mnesa_db_TARGET_TIMESTAMP.dump.gpg"

# Step 2: Terminate active connections
psql -h "${DB_HOST}" -U "${DB_USER}" -d postgres -c \
    "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'mnesa' AND pid <> pg_backend_pid();"

# Step 3: Recreate clean target database
psql -h "${DB_HOST}" -U "${DB_USER}" -d postgres -c "DROP DATABASE IF EXISTS mnesa;"
psql -h "${DB_HOST}" -U "${DB_USER}" -d postgres -c "CREATE DATABASE mnesa OWNER ${DB_USER};"

# Step 4: Execute pg_restore with clean schema
pg_restore -h "${DB_HOST}" \
           -p "${DB_PORT}" \
           -U "${DB_USER}" \
           -d mnesa \
           -v \
           --no-owner \
           --no-privileges \
           /tmp/mnesa_restore.dump

# Step 5: Clean up decrypted temporary file
shred -u /tmp/mnesa_restore.dump
```

### 4.2 Post-Restoration Verification Checklist
Immediately after executing a database restoration, run the following verification queries:

```sql
-- 1. Verify Flyway migration consistency
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;

-- 2. Verify relational row counts are sane
SELECT count(*) AS user_count FROM app_users;
SELECT count(*) AS opportunity_count FROM opportunities;
SELECT count(*) AS reminder_count FROM reminders;
SELECT count(*) AS attachment_count FROM attachments;

-- 3. Verify foreign key integrity
SELECT c.id FROM opportunities c LEFT JOIN app_users u ON c.user_id = u.id WHERE u.id IS NULL;
-- (Must return 0 rows)
```

---

## 5. MinIO / S3 Object Storage Backup & Recovery

### 5.1 Object Store Replication
- **Tool:** MinIO Client (`mc`)
- **Bucket:** `mnesa-attachments`

```bash
# Periodic sync to offsite replica
mc mirror --overwrite --remove \
    local-minio/mnesa-attachments \
    backup-s3/mnesa-attachments-cold-backup/
```

### 5.2 Bucket Restoration Procedure
```bash
# Restore objects from backup destination
mc mirror --overwrite \
    backup-s3/mnesa-attachments-cold-backup/ \
    local-minio/mnesa-attachments
```

---

## 6. Disaster Recovery Playbooks

### Playbook A: Failed Flyway Migration Recovery
If a database migration fails during deployment:
1. Stop backend service instances (`docker compose stop backend`).
2. Inspect the failed script error in application logs.
3. If migration was partially applied, manually repair the affected table or revert according to migration spec.
4. Delete the failed row from `flyway_schema_history`:
   ```sql
   DELETE FROM flyway_schema_history WHERE version = 'FAILED_VERSION' AND success = false;
   ```
5. Apply corrected migration and restart backend.

### Playbook B: Catastrophic Hardware Failure (Full Rebuild)
1. Provision fresh Linux host.
2. Clone repository: `git clone https://github.com/Balashanmugam30/mnesa.git`.
3. Restore environment secrets (`.env.production`).
4. Initialize infrastructure via Docker Compose.
5. Execute Section 4.1 to restore latest PostgreSQL dump.
6. Execute Section 5.2 to restore MinIO media bucket.
7. Start services: `docker compose up -d`.
8. Execute smoke tests against `/actuator/health` and `/api/v1/health`.

---
*Backup & Disaster Recovery runbook approved for production operations.*
