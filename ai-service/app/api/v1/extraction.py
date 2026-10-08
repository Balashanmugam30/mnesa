from fastapi import APIRouter, HTTPException
from app.core.config import settings
from app.providers.gemini import GeminiAIProvider
from app.providers.ollama import OllamaAIProvider
from app.schemas.extraction import ExtractionPayload, ExtractionResult

router = APIRouter(prefix="/api/v1", tags=["Extraction"])


def get_active_provider():
    if settings.AI_PROVIDER == "ollama":
        return OllamaAIProvider()
    return GeminiAIProvider()


@router.post("/extract", response_model=ExtractionResult)
async def extract_opportunity_endpoint(payload: ExtractionPayload):
    provider = get_active_provider()
    result = await provider.extract_opportunity(payload)
    if not result.success and not result.opportunity:
        raise HTTPException(
            status_code=502,
            detail=result.error_message or "Opportunity extraction failed",
        )
    return result
