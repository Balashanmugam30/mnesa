from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_extract_endpoint_success():
    payload = {
        "raw_text": "Join the HackMIT 2026 Hackathon! Register by October 15. All college students welcome.",
        "source_url": "https://hackmit.org",
        "user_notes": "Looks interesting for autumn",
    }

    response = client.post("/api/v1/extract", json=payload)
    assert response.status_code == 200
    data = response.json()

    assert data["success"] is True
    assert "opportunity" in data
    assert data["opportunity"]["title"] is not None
    assert data["opportunity"]["opportunity_type"] == "HACKATHON"
    assert data["opportunity"]["confidence_score"] >= 0.0
    assert "latency_ms" in data


def test_extract_endpoint_validation_error():
    # raw_text under 5 characters triggers Pydantic validation failure
    payload = {
        "raw_text": "hi",
    }

    response = client.post("/api/v1/extract", json=payload)
    assert response.status_code == 422
