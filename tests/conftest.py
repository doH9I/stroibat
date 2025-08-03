"""
Pytest configuration and fixtures for Construction CRM tests
"""
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from fastapi.testclient import TestClient

from app.main import app
from app.core.database import get_db, Base
from app.core.config import settings

# Create test database URL
TEST_DATABASE_URL = f"mysql+pymysql://{settings.DB_USER}:{settings.DB_PASSWORD}@{settings.DB_HOST}:{settings.DB_PORT}/test_{settings.DB_NAME}"

# Create test engine
engine = create_engine(TEST_DATABASE_URL)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def override_get_db():
    """Override database dependency for testing."""
    try:
        db = TestingSessionLocal()
        yield db
    finally:
        db.close()


@pytest.fixture(scope="session")
def db_engine():
    """Create test database engine."""
    return engine


@pytest.fixture(scope="function")
def db_session(db_engine):
    """Create a fresh database session for each test."""
    Base.metadata.create_all(bind=db_engine)
    session = TestingSessionLocal()
    yield session
    session.close()
    Base.metadata.drop_all(bind=db_engine)


@pytest.fixture(scope="function")
def client(db_session):
    """Create test client with database override."""
    app.dependency_overrides[get_db] = override_get_db
    with TestClient(app) as test_client:
        yield test_client
    app.dependency_overrides.clear()