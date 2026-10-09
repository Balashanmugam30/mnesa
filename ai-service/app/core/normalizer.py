import json
import re
from html import unescape
from html.parser import HTMLParser
from typing import Any, Dict, List, Optional
from urllib.parse import urlparse
from pydantic import BaseModel, Field

MAX_NORMALIZED_TEXT_CHARS = 15000

DISCARD_TAGS = {"script", "style", "nav", "footer", "header", "noscript", "svg", "aside", "iframe"}
BLOCK_TAGS = {"p", "h1", "h2", "h3", "h4", "h5", "h6", "li", "tr", "div", "section", "article"}


class NormalizedContent(BaseModel):
    title: Optional[str] = None
    meta_description: Optional[str] = None
    og_title: Optional[str] = None
    og_description: Optional[str] = None
    og_site_name: Optional[str] = None
    canonical_url: Optional[str] = None
    json_ld_data: List[Dict[str, Any]] = Field(default_factory=list)
    clean_text: str = ""
    source_domain: Optional[str] = None
    best_candidate_title: str = ""


class OpportunityHTMLParser(HTMLParser):
    """
    Standard-library HTMLParser designed to extract page metadata, JSON-LD,
    and clean readable text while stripping boilerplate, navigation, and scripts.
    """

    def __init__(self):
        super().__init__()
        self.in_discard_tag = False
        self.current_tag_stack: List[str] = []
        self.in_title = False
        self.in_json_ld = False

        self.title_parts: List[str] = []
        self.json_ld_parts: List[str] = []
        self.text_chunks: List[str] = []

        self.meta_description: Optional[str] = None
        self.og_title: Optional[str] = None
        self.og_description: Optional[str] = None
        self.og_site_name: Optional[str] = None
        self.canonical_url: Optional[str] = None
        self.json_ld_objects: List[Dict[str, Any]] = []

    def handle_starttag(self, tag: str, attrs: List[tuple]):
        tag_lower = tag.lower()
        self.current_tag_stack.append(tag_lower)
        attr_dict = {k.lower(): v for k, v in attrs if v is not None}

        if tag_lower in DISCARD_TAGS:
            if tag_lower == "script" and attr_dict.get("type", "").lower() == "application/ld+json":
                self.in_json_ld = True
                self.json_ld_parts = []
            else:
                self.in_discard_tag = True
            return

        if tag_lower == "title":
            self.in_title = True
            return

        if tag_lower == "meta":
            name = attr_dict.get("name", "").lower()
            prop = attr_dict.get("property", "").lower()
            content = attr_dict.get("content", "")

            if name == "description" or prop == "description":
                self.meta_description = content.strip()
            elif prop == "og:title":
                self.og_title = content.strip()
            elif prop == "og:description":
                self.og_description = content.strip()
            elif prop == "og:site_name":
                self.og_site_name = content.strip()

        elif tag_lower == "link":
            rel = attr_dict.get("rel", "").lower()
            if rel == "canonical":
                self.canonical_url = attr_dict.get("href", "").strip()

        if tag_lower in BLOCK_TAGS:
            self.text_chunks.append("\n")

    def handle_endtag(self, tag: str):
        tag_lower = tag.lower()
        if self.current_tag_stack and self.current_tag_stack[-1] == tag_lower:
            self.current_tag_stack.pop()

        if tag_lower == "title":
            self.in_title = False
        elif tag_lower == "script" and self.in_json_ld:
            self.in_json_ld = False
            raw_json = "".join(self.json_ld_parts).strip()
            if raw_json:
                try:
                    parsed = json.loads(raw_json)
                    if isinstance(parsed, list):
                        self.json_ld_objects.extend([item for item in parsed if isinstance(item, dict)])
                    elif isinstance(parsed, dict):
                        self.json_ld_objects.append(parsed)
                except Exception:
                    pass
        elif tag_lower in DISCARD_TAGS:
            # Check if we are still inside another discard tag
            self.in_discard_tag = any(t in DISCARD_TAGS for t in self.current_tag_stack)

        if tag_lower in BLOCK_TAGS:
            self.text_chunks.append("\n")

    def handle_data(self, data: str):
        if self.in_json_ld:
            self.json_ld_parts.append(data)
            return

        if self.in_discard_tag:
            return

        if self.in_title:
            self.title_parts.append(data)
            return

        cleaned = data.strip()
        if cleaned:
            self.text_chunks.append(" " + cleaned + " ")


def extract_domain(url: Optional[str]) -> Optional[str]:
    if not url:
        return None
    try:
        parsed = urlparse(url)
        return parsed.netloc.lower().split(":")[0]
    except Exception:
        return None


def normalize_html_content(html: str, source_url: Optional[str] = None) -> NormalizedContent:
    """
    Parses raw HTML and returns normalized text, Open Graph metadata, and JSON-LD.
    """
    parser = OpportunityHTMLParser()
    try:
        parser.feed(html)
    except Exception:
        pass  # Best-effort parse

    page_title = unescape("".join(parser.title_parts)).strip() if parser.title_parts else None

    # Assemble cleaned body text
    raw_text = "".join(parser.text_chunks)
    raw_text = unescape(raw_text)

    # Normalize multiple newlines and spaces
    lines = [re.sub(r"[ \t]+", " ", line.strip()) for line in raw_text.splitlines()]
    clean_lines = [line for line in lines if line]
    clean_text = "\n".join(clean_lines)

    # Limit text length
    if len(clean_text) > MAX_NORMALIZED_TEXT_CHARS:
        clean_text = clean_text[:MAX_NORMALIZED_TEXT_CHARS] + "\n...[Content truncated for analysis]..."

    # Determine best candidate title by precedence
    best_title = (
        parser.og_title or
        page_title or
        (parser.json_ld_objects[0].get("title") if parser.json_ld_objects and isinstance(parser.json_ld_objects[0].get("title"), str) else None) or
        "Shared Opportunity"
    )

    domain = extract_domain(source_url or parser.canonical_url)

    return NormalizedContent(
        title=page_title,
        meta_description=parser.meta_description,
        og_title=parser.og_title,
        og_description=parser.og_description,
        og_site_name=parser.og_site_name,
        canonical_url=parser.canonical_url,
        json_ld_data=parser.json_ld_objects,
        clean_text=clean_text,
        source_domain=domain,
        best_candidate_title=best_title,
    )
