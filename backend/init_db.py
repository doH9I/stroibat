#!/usr/bin/env python3
"""
Standalone script to initialize the Construction CRM database
"""
import sys
import os

# Add the project root to Python path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app.core.database import init_db
from app.services.init_db import init_default_data
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def main():
    """Initialize database and default data."""
    try:
        logger.info("Initializing Construction CRM database...")
        
        # Create tables
        init_db()
        
        # Add default data
        init_default_data()
        
        logger.info("Database initialization completed successfully!")
        logger.info("Default admin credentials:")
        logger.info("  Username: admin")
        logger.info("  Email: admin@construction-crm.com")
        logger.info("  Password: password")
        logger.info("Please change the admin password after first login!")
        
    except Exception as e:
        logger.error(f"Database initialization failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()