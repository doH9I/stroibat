from sqlalchemy import Column, Integer, String, DateTime, Enum, Text, DECIMAL, ForeignKey, Date
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class EquipmentStatus(str, enum.Enum):
    AVAILABLE = "available"
    IN_USE = "in_use"
    MAINTENANCE = "maintenance"
    RETIRED = "retired"


class Equipment(Base):
    __tablename__ = "equipment"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    model = Column(String(100), nullable=True)
    serial_number = Column(String(100), nullable=True)
    purchase_date = Column(Date, nullable=True)
    purchase_price = Column(DECIMAL(15, 2), nullable=True)
    current_value = Column(DECIMAL(15, 2), nullable=True)
    status = Column(Enum(EquipmentStatus), default=EquipmentStatus.AVAILABLE)
    location = Column(String(255), nullable=True)
    assigned_to = Column(Integer, ForeignKey("users.id"), nullable=True)
    maintenance_schedule = Column(Text, nullable=True)
    last_maintenance = Column(Date, nullable=True)
    next_maintenance = Column(Date, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    assigned_user = relationship("User", foreign_keys=[assigned_to])
    project_usage = relationship("ProjectEquipment", back_populates="equipment")
    
    def __repr__(self):
        return f"<Equipment(id={self.id}, name='{self.name}', status='{self.status}')>"


class ProjectEquipment(Base):
    __tablename__ = "project_equipment"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=False)
    equipment_id = Column(Integer, ForeignKey("equipment.id"), nullable=False)
    start_date = Column(Date, nullable=False)
    end_date = Column(Date, nullable=True)
    daily_rate = Column(DECIMAL(10, 2), nullable=True)
    total_cost = Column(DECIMAL(15, 2), nullable=True)
    notes = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="equipment_usage")
    equipment = relationship("Equipment", back_populates="project_usage")
    
    def __repr__(self):
        return f"<ProjectEquipment(id={self.id}, project_id={self.project_id}, equipment_id={self.equipment_id})>"