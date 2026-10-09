import pytest
from app.core.fetcher import (
    BLOCKED_HOSTNAMES,
    SafeWebFetcher,
    SsrfViolationError,
    UnsupportedContentTypeError,
    validate_url_and_ip,
)


def test_validate_url_and_ip_blocks_loopback():
    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://127.0.0.1/admin")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://localhost:8080/secret")


def test_validate_url_and_ip_blocks_private_subnets():
    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://10.0.0.1/dashboard")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://192.168.1.1/router")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://172.16.0.5/api")


def test_validate_url_and_ip_blocks_cloud_metadata():
    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://169.254.169.254/latest/meta-data/")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("http://metadata.google.internal/computeMetadata/v1/")


def test_validate_url_and_ip_blocks_prohibited_schemes():
    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("file:///etc/passwd")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("ftp://example.com/file.txt")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("javascript:alert(1)")

    with pytest.raises(SsrfViolationError):
        validate_url_and_ip("data:text/html,<html></html>")


def test_validate_url_and_ip_blocks_internal_docker_services():
    for name in ("postgres", "valkey", "minio", "backend"):
        with pytest.raises(SsrfViolationError):
            validate_url_and_ip(f"http://{name}:5432")


def test_validate_url_and_ip_allows_valid_public_domain():
    # Valid public domain test (e.g. google.com or github.com)
    host, ips = validate_url_and_ip("https://github.com/features")
    assert host == "github.com"
    assert len(ips) > 0
    assert not any(ip.is_private for ip in ips)
