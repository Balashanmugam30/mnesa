from abc import ABC, abstractmethod
from app.schemas.extraction import ExtractionPayload, ExtractionResult


class BaseAIProvider(ABC):
    """
    Abstract Base Class for all MNESA intelligence and extraction providers.
    """

    @property
    @abstractmethod
    def provider_name(self) -> str:
        """Returns the canonical provider identifier (e.g. 'gemini', 'ollama', 'mock')."""
        pass

    @abstractmethod
    async def extract_opportunity(self, payload: ExtractionPayload) -> ExtractionResult:
        """
        Extracts structured opportunity details from the untrusted payload.
        """
        pass

    async def extract_candidates(self, payload: ExtractionPayload):
        """
        Extracts multiple opportunity proposals from dense multi-item text or screenshots.
        Defaults to returning a list with the single primary extracted opportunity.
        """
        result = await self.extract_opportunity(payload)
        return [result.opportunity] if result.opportunity else []

