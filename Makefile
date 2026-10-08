# MNESA Developer Makefile

.PHONY: help up down status test test-backend test-android test-ai test-web build

help:
	@echo "MNESA Commands:"
	@echo "  make up            - Start PostgreSQL, Valkey, and MinIO via Docker Compose"
	@echo "  make down          - Stop Docker Compose services"
	@echo "  make status        - Check container health and status"
	@echo "  make test-backend  - Run Spring Boot tests"
	@echo "  make test-android  - Run Android unit tests & assemble debug APK"
	@echo "  make test-ai       - Run Python AI service tests"
	@echo "  make test-web      - Build and test Next.js web platform"
	@echo "  make test          - Run test suites across all 4 tiers"

up:
	docker compose -f infrastructure/docker/compose.dev.yml up -d postgres valkey minio

down:
	docker compose -f infrastructure/docker/compose.dev.yml down

status:
	docker compose -f infrastructure/docker/compose.dev.yml ps

test-backend:
	cd backend && ./gradlew test

test-android:
	cd android && ./gradlew testDebugUnitTest assembleDebug

test-ai:
	cd ai-service && pytest -v

test-web:
	cd website && npm run build

test: test-backend test-ai test-android test-web
