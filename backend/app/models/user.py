from sqlalchemy import Column, Integer, String, Boolean, DateTime, Enum, Text
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class UserRole(str, enum.Enum):
    ADMIN = "admin"
    MANAGER = "manager"
    EMPLOYEE = "employee"
    CLIENT = "client"


class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, nullable=False, index=True)
    email = Column(String(100), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    first_name = Column(String(50), nullable=False)
    last_name = Column(String(50), nullable=False)
    role = Column(Enum(UserRole), nullable=False, default=UserRole.EMPLOYEE)
    phone = Column(String(20), nullable=True)
    avatar = Column(String(255), nullable=True)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    managed_projects = relationship("Project", foreign_keys="Project.manager_id", back_populates="manager")
    assigned_tasks = relationship("ProjectTask", foreign_keys="ProjectTask.assigned_to", back_populates="assigned_user")
    time_entries = relationship("TimeTracking", foreign_keys="TimeTracking.user_id", back_populates="user")
    created_estimates = relationship("Estimate", foreign_keys="Estimate.created_by", back_populates="creator")
    created_invoices = relationship("Invoice", foreign_keys="Invoice.created_by", back_populates="creator")
    uploaded_documents = relationship("Document", foreign_keys="Document.uploaded_by", back_populates="uploader")
    notifications = relationship("Notification", back_populates="user")
    approved_time_entries = relationship("TimeTracking", foreign_keys="TimeTracking.approved_by", back_populates="approver")
    
    @property
    def full_name(self) -> str:
        return f"{self.first_name} {self.last_name}"
    
    def __repr__(self):
        return f"<User(id={self.id}, username='{self.username}', email='{self.email}')>"