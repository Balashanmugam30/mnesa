import base64
import binascii
from typing import Optional, Tuple
from app.core.logging import logger

MAX_IMAGE_BYTES = 10 * 1024 * 1024  # 10 MB

# Magic bytes signatures for supported image formats
MAGIC_BYTES = {
    "image/png": b"\x89PNG\r\n\x1a\n",
    "image/jpeg": b"\xff\xd8\xff",
    "image/webp": b"RIFF",  # WEBP starts with RIFF....WEBP
}


class OcrError(Exception):
    pass


class InvalidImageFormatError(OcrError):
    pass


class ImageTooLargeError(OcrError):
    pass


def validate_image_bytes(image_data: bytes, declared_mime_type: Optional[str] = None) -> str:
    """
    Validates magic bytes against known image signatures and enforces size limits.
    Returns detected MIME type.
    """
    if len(image_data) > MAX_IMAGE_BYTES:
        raise ImageTooLargeError(f"Image size ({len(image_data)} bytes) exceeds maximum limit of {MAX_IMAGE_BYTES} bytes")

    if image_data.startswith(b"\x89PNG\r\n\x1a\n"):
        return "image/png"
    elif image_data.startswith(b"\xff\xd8\xff"):
        return "image/jpeg"
    elif len(image_data) >= 12 and image_data.startswith(b"RIFF") and image_data[8:12] == b"WEBP":
        return "image/webp"

    raise InvalidImageFormatError("Unrecognized image signature or corrupted binary header")


class OcrProcessor:
    """
    Adapter for Optical Character Recognition from image payloads.
    Provides graceful fallback if local OCR engines are not available in environment.
    """

    def __init__(self):
        self._tesseract_available = False
        try:
            import pytesseract  # type: ignore
            self._pytesseract = pytesseract
            self._tesseract_available = True
        except ImportError:
            self._pytesseract = None

    @property
    def is_available(self) -> bool:
        return self._tesseract_available

    def extract_text_from_base64(self, b64_data: str, declared_mime: Optional[str] = None) -> Tuple[Optional[str], Optional[str]]:
        """
        Extracts readable text from base64 image data.

        Returns:
            Tuple of (extracted_text, error_message_or_warning)
        """
        if not b64_data:
            return None, "Empty image data"

        try:
            raw_bytes = base64.b64decode(b64_data, validate=True)
        except binascii.Error:
            return None, "Invalid base64 payload"

        try:
            detected_mime = validate_image_bytes(raw_bytes, declared_mime)
        except OcrError as e:
            return None, str(e)

        if not self._tesseract_available:
            logger.info("OCR engine (pytesseract) not installed in runtime environment. Returning graceful fallback.")
            return None, "OCR engine not available in environment (screenshot preserved)"

        try:
            import io
            from PIL import Image  # type: ignore

            image = Image.open(io.BytesIO(raw_bytes))
            # Limit dimension expansion attacks (e.g. decompression bombs)
            if image.width * image.height > 25000000:  # 25 MP cap
                return None, "Image resolution exceeds safety threshold"

            text = self._pytesseract.image_to_string(image)
            return text.strip() if text else None, None
        except Exception as e:
            logger.warning(f"OCR execution failed: {str(e)}")
            return None, f"OCR processing failed: {str(e)}"
