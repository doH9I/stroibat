"""
Basic tests for Construction CRM application
"""
import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_root_endpoint():
    """Test the root endpoint."""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert data["message"] == "Construction CRM API"


def test_health_check():
    """Test the health check endpoint."""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "app_name" in data
    assert "version" in data


def test_docs_endpoint():
    """Test that API docs are accessible in debug mode."""
    response = client.get("/docs")
    # Should either return 200 (if DEBUG=True) or 404 (if DEBUG=False)
    assert response.status_code in [200, 404]


def test_login_endpoint_structure():
    """Test that login endpoint exists and has correct structure."""
    # Test with no credentials
    response = client.post("/api/v1/auth/login", data={})
    # Should return 422 (validation error) for missing credentials
    assert response.status_code == 422