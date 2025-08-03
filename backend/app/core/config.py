from pydantic_settings import BaseSettings
from decouple import config
from typing import Optional


class Settings(BaseSettings):
    # Database settings
    DB_HOST: str = config("DB_HOST", default="localhost")
    DB_PORT: int = config("DB_PORT", default=3306, cast=int)
    DB_USER: str = config("DB_USER", default="root")
    DB_PASSWORD: str = config("DB_PASSWORD", default="")
    DB_NAME: str = config("DB_NAME", default="construction_crm")
    
    # JWT settings
    SECRET_KEY: str = config("SECRET_KEY", default="your-secret-key-here-change-in-production")
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = config("ACCESS_TOKEN_EXPIRE_MINUTES", default=30, cast=int)
    
    # Application settings
    APP_NAME: str = "Construction CRM"
    APP_VERSION: str = "2.0.0"
    DEBUG: bool = config("DEBUG", default=False, cast=bool)
    
    # File upload settings
    UPLOAD_DIR: str = config("UPLOAD_DIR", default="./uploads")
    MAX_FILE_SIZE: int = config("MAX_FILE_SIZE", default=10485760, cast=int)  # 10MB
    
    # Email settings (for notifications)
    SMTP_HOST: Optional[str] = config("SMTP_HOST", default=None)
    SMTP_PORT: int = config("SMTP_PORT", default=587, cast=int)
    SMTP_USER: Optional[str] = config("SMTP_USER", default=None)
    SMTP_PASSWORD: Optional[str] = config("SMTP_PASSWORD", default=None)
    
    @property
    def database_url(self) -> str:
        return f"mysql+pymysql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"
    
    class Config:
        env_file = ".env"


settings = Settings()