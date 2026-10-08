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
