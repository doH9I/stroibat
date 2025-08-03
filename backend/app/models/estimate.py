from sqlalchemy import Column, Integer, String, DateTime, Enum, Text, DECIMAL, ForeignKey, Date
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class EstimateStatus(str, enum.Enum):
    DRAFT = "draft"
    SENT = "sent"
    APPROVED = "approved"
    REJECTED = "rejected"
    EXPIRED = "expired"


class Estimate(Base):
    __tablename__ = "estimates"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=False)
    estimate_number = Column(String(100), unique=True, nullable=False, index=True)
    title = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    total_amount = Column(DECIMAL(15, 2), nullable=False)
    tax_rate = Column(DECIMAL(5, 2), default=0)
    tax_amount = Column(DECIMAL(15, 2), default=0)
    grand_total = Column(DECIMAL(15, 2), nullable=False)
    status = Column(Enum(EstimateStatus), default=EstimateStatus.DRAFT)
    valid_until = Column(Date, nullable=True)
    created_by = Column(Integer, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="estimates")
    creator = relationship("User", foreign_keys=[created_by], back_populates="created_estimates")
    items = relationship("EstimateItem", back_populates="estimate", cascade="all, delete-orphan")
    invoices = relationship("Invoice", back_populates="estimate")
    
    def __repr__(self):
        return f"<Estimate(id={self.id}, estimate_number='{self.estimate_number}', status='{self.status}')>"


class EstimateItem(Base):
    __tablename__ = "estimate_items"
    
    id = Column(Integer, primary_key=True, index=True)
    estimate_id = Column(Integer, ForeignKey("estimates.id"), nullable=False)
    description = Column(String(500), nullable=False)
    quantity = Column(DECIMAL(10, 2), nullable=False)
    unit = Column(String(50), nullable=False)
    unit_price = Column(DECIMAL(10, 2), nullable=False)
    total_price = Column(DECIMAL(15, 2), nullable=False)
    category = Column(String(100), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    # Relationships
    estimate = relationship("Estimate", back_populates="items")
    
    def __repr__(self):
        return f"<EstimateItem(id={self.id}, description='{self.description}', quantity={self.quantity})>"