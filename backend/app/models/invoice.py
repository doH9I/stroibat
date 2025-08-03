from sqlalchemy import Column, Integer, String, DateTime, Enum, Text, DECIMAL, ForeignKey, Date
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class InvoiceStatus(str, enum.Enum):
    DRAFT = "draft"
    SENT = "sent"
    PAID = "paid"
    OVERDUE = "overdue"
    CANCELLED = "cancelled"


class Invoice(Base):
    __tablename__ = "invoices"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=False)
    invoice_number = Column(String(100), unique=True, nullable=False, index=True)
    estimate_id = Column(Integer, ForeignKey("estimates.id"), nullable=True)
    amount = Column(DECIMAL(15, 2), nullable=False)
    tax_amount = Column(DECIMAL(15, 2), default=0)
    total_amount = Column(DECIMAL(15, 2), nullable=False)
    status = Column(Enum(InvoiceStatus), default=InvoiceStatus.DRAFT)
    issue_date = Column(Date, nullable=False)
    due_date = Column(Date, nullable=False)
    paid_date = Column(Date, nullable=True)
    payment_method = Column(String(100), nullable=True)
    notes = Column(Text, nullable=True)
    created_by = Column(Integer, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="invoices")
    estimate = relationship("Estimate", back_populates="invoices")
    creator = relationship("User", foreign_keys=[created_by], back_populates="created_invoices")
    
    def __repr__(self):
        return f"<Invoice(id={self.id}, invoice_number='{self.invoice_number}', status='{self.status}')>"