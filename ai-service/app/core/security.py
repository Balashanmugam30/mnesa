import re
from typing import List, Tuple

INJECTION_PATTERNS = [
    r"ignore\s+(all\s+)?(previous|prior)\s+instructions",
    r"system\s+(prompt|override|message)",
    r"you\s+are\s+now\s+(in\s+developer\s+mode|dan)",
    r"disregard\s+all\s+rules",
    r"jailbreak",
    r"reveal\s+(the\s+)?system\s+prompt",
    r"as\s+an\s+unfiltered\s+ai",
]

COMPILED_INJECTION_PATTERNS = [
    re.compile(pattern, re.IGNORECASE) for pattern in INJECTION_PATTERNS
]


def sanitize_untrusted_input(text: str) -> Tuple[str, List[str]]:
    """
    Sanitizes external untrusted text to defend against prompt injection
    and strips control characters.

    Returns:
        Tuple of (encapsulated_text, detected_security_flags)
    """
    if not text:
        return "<untrusted_content></untrusted_content>", []

    # 1. Strip null bytes and non-printable control characters
    sanitized = "".join(ch for ch in text if ch == "\n" or ch == "\t" or ch == "\r" or ch >= " ")

    # 2. Detect potential injection patterns
    flags: List[str] = []
    for pattern in COMPILED_INJECTION_PATTERNS:
        if pattern.search(sanitized):
            flags.append(f"SUSPICIOUS_PATTERN_DETECTED:{pattern.pattern}")

    # 3. Escape XML delimiters that might prematurely close the container
    escaped = sanitized.replace("</untrusted_content>", "&lt;/untrusted_content&gt;")

    # 4. Wrap in strict XML boundary tag
    wrapped = f"<untrusted_content>\n{escaped}\n</untrusted_content>"

    return wrapped, flags
