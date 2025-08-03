"""
Database initialization service for Construction CRM
"""
import logging
from sqlalchemy.orm import Session
from app.core.database import SessionLocal, engine
from app.core.security import get_password_hash
from app.models import *  # Import all models

logger = logging.getLogger(__name__)


def init_default_data():
    """Initialize database with default data."""
    db = SessionLocal()
    try:
        # Check if admin user already exists
        admin_user = db.query(User).filter(User.username == "admin").first()
        if admin_user:
            logger.info("Admin user already exists, skipping initialization")
            return

        # Create default admin user
        admin_password = get_password_hash("password")
        admin_user = User(
            username="admin",
            email="admin@construction-crm.com",
            password_hash=admin_password,
            first_name="Admin",
            last_name="User",
            role=UserRole.ADMIN,
            is_active=True
        )
        db.add(admin_user)
        db.commit()
        db.refresh(admin_user)
        logger.info("Admin user created successfully")

        # Create default settings
        default_settings = [
            Setting(
                setting_key="company_name",
                setting_value="Construction CRM",
                description="Company name"
            ),
            Setting(
                setting_key="company_address",
                setting_value="",
                description="Company address"
            ),
            Setting(
                setting_key="company_phone",
                setting_value="",
                description="Company phone"
            ),
            Setting(
                setting_key="company_email",
                setting_value="",
                description="Company email"
            ),
            Setting(
                setting_key="tax_rate",
                setting_value="20",
                description="Default tax rate percentage"
            ),
            Setting(
                setting_key="currency",
                setting_value="RUB",
                description="Default currency"
            ),
            Setting(
                setting_key="timezone",
                setting_value="Europe/Moscow",
                description="Default timezone"
            )
        ]

        for setting in default_settings:
            existing_setting = db.query(Setting).filter(
                Setting.setting_key == setting.setting_key
            ).first()
            if not existing_setting:
                db.add(setting)

        db.commit()
        logger.info("Default settings created successfully")

        # Create sample client (optional)
        sample_client = Client(
            company_name="ООО Пример",
            contact_person="Иван Иванов",
            email="client@example.com",
            phone="+7 (999) 123-45-67",
            address="г. Москва, ул. Примерная, д. 1",
            status=ClientStatus.ACTIVE
        )
        db.add(sample_client)
        db.commit()
        logger.info("Sample client created successfully")

        # Create sample materials
        sample_materials = [
            Material(
                name="Кирпич керамический",
                description="Кирпич керамический рядовой",
                unit="шт",
                price_per_unit=25.50,
                supplier="ОАО Кирпичный завод",
                category="Стеновые материалы",
                current_stock=1000,
                min_stock=100
            ),
            Material(
                name="Цемент М400",
                description="Портландцемент М400",
                unit="кг",
                price_per_unit=8.20,
                supplier="ООО Цементпром",
                category="Вяжущие",
                current_stock=5000,
                min_stock=500
            ),
            Material(
                name="Арматура А500С",
                description="Арматура периодическая диаметр 12мм",
                unit="м",
                price_per_unit=85.30,
                supplier="Металлторг",
                category="Металлические изделия",
                current_stock=2000,
                min_stock=200
            )
        ]

        for material in sample_materials:
            db.add(material)

        db.commit()
        logger.info("Sample materials created successfully")

        logger.info("Database initialization completed successfully")

    except Exception as e:
        logger.error(f"Error initializing database: {e}")
        db.rollback()
        raise
    finally:
        db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    init_default_data()