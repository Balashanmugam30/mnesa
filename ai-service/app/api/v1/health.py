from datetime import datetime, timezone
from fastapi import APIRouter
from app.core.config import settings

router = APIRouter(tags=["Health"])


@router.get("/health")
def get_health():
    return {
        "status": "UP",
        "service": "mnesa-ai-service",
        "version": "0.1.0",
        "environment": settings.ENVIRONMENT,
        "active_provider": settings.AI_PROVIDER,
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
