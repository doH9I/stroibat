from sqlalchemy import Column, Integer, String, DateTime, Enum, Text
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class ClientStatus(str, enum.Enum):
    ACTIVE = "active"
    INACTIVE = "inactive"
    POTENTIAL = "potential"


class Client(Base):
    __tablename__ = "clients"
    
    id = Column(Integer, primary_key=True, index=True)
    company_name = Column(String(100), nullable=True)
    contact_person = Column(String(100), nullable=False)
    email = Column(String(100), nullable=False, index=True)
    phone = Column(String(20), nullable=True)
    address = Column(Text, nullable=True)
    tax_number = Column(String(50), nullable=True)
    bank_details = Column(Text, nullable=True)
    notes = Column(Text, nullable=True)
    status = Column(Enum(ClientStatus), default=ClientStatus.ACTIVE)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    projects = relationship("Project", back_populates="client")
    
    @property
    def display_name(self) -> str:
        return self.company_name if self.company_name else self.contact_person
    
    def __repr__(self):
        return f"<Client(id={self.id}, company_name='{self.company_name}', contact_person='{self.contact_person}')>"