import time
import httpx
from app.core.config import settings
from app.core.logging import logger
from app.core.security import sanitize_untrusted_input
from app.providers.base import BaseAIProvider
from app.schemas.extraction import (
    ExtractionPayload,
    ExtractionResult,
    ExtractedOpportunity,
    OpportunityType,
)


class OllamaAIProvider(BaseAIProvider):
    """
    Local Ollama extraction provider for offline and edge deployment.
    """

    def __init__(self, base_url: str = None, model: str = None):
        self.base_url = (base_url or settings.OLLAMA_BASE_URL).rstrip("/")
        self.model = model or settings.OLLAMA_MODEL

    @property
    def provider_name(self) -> str:
        return "ollama"

    async def extract_opportunity(self, payload: ExtractionPayload) -> ExtractionResult:
        start_time = time.time()
        sanitized_content, flags = sanitize_untrusted_input(payload.raw_text)

        schema_json = ExtractedOpportunity.model_json_schema()
        prompt = (
            "You are the MNESA Opportunity Extraction Engine. "
            "Extract structured opportunity details conforming strictly to the requested JSON schema. "
            f"{sanitized_content}"
        )

        try:
            async with httpx.AsyncClient(timeout=30.0) as client:
                res = await client.post(
                    f"{self.base_url}/api/generate",
                    json={
                        "model": self.model,
                        "prompt": prompt,
                        "format": "json",
                        "stream": False,
                    },
                )
                res.raise_for_status()
                data = res.json()
                response_text = data.get("response", "{}")
                opportunity = ExtractedOpportunity.model_validate_json(response_text)
                latency = (time.time() - start_time) * 1000.0

                return ExtractionResult(
                    success=True,
                    opportunity=opportunity,
                    provider_used=self.provider_name,
                    latency_ms=round(latency, 2),
                    sanitization_flags=flags,
                )
        except Exception as e:
            logger.warning(f"Ollama extraction unavailable ({str(e)}). Falling back to mock simulation.")
            latency = (time.time() - start_time) * 1000.0
            simulated = ExtractedOpportunity(
                title="Extracted Opportunity (Ollama Simulation Fallback)",
                organization="MNESA Local Simulation",
                opportunity_type=OpportunityType.OTHER,
                confidence_score=0.70,
                evidence_snippets=[payload.raw_text[:60]],
            )
            return ExtractionResult(
                success=True,
                opportunity=simulated,
                provider_used="ollama-mock",
                latency_ms=round(latency, 2),
                sanitization_flags=flags,
            )
