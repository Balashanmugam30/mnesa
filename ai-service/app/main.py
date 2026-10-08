from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.v1.health import router as health_router
from app.api.v1.extraction import router as extraction_router
from app.core.config import settings
from app.core.logging import configure_logging, logger


@asynccontextmanager
async def lifespan(app: FastAPI):
    configure_logging()
    logger.info(
        f"MNESA AI Service initialized [Env: {settings.ENVIRONMENT}, Provider: {settings.AI_PROVIDER}]"
    )
    yield
    logger.info("MNESA AI Service shutting down")


app = FastAPI(
    title="MNESA AI Opportunity Extraction Service",
    version="0.1.0",
    description="Intelligence service providing zero-trust content sanitization and structured opportunity extraction",
    lifespan=lifespan,
)

# CORS middleware for local frontend and backend clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount routes
app.include_router(health_router)
app.include_router(health_router, prefix="/api/v1")
app.include_router(extraction_router)


@app.get("/")
def root():
    return {
        "service": "mnesa-ai-service",
        "status": "UP",
        "documentation": "/docs",
    }
