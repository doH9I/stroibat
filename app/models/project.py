from sqlalchemy import Column, Integer, String, DateTime, Enum, Text, Date, DECIMAL, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base
import enum


class ProjectStatus(str, enum.Enum):
    PLANNING = "planning"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    ON_HOLD = "on_hold"
    CANCELLED = "cancelled"


class ProjectPriority(str, enum.Enum):
    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    URGENT = "urgent"


class TaskStatus(str, enum.Enum):
    PENDING = "pending"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    CANCELLED = "cancelled"


class Project(Base):
    __tablename__ = "projects"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    client_id = Column(Integer, ForeignKey("clients.id"), nullable=True)
    manager_id = Column(Integer, ForeignKey("users.id"), nullable=True)
    start_date = Column(Date, nullable=True)
    end_date = Column(Date, nullable=True)
    budget = Column(DECIMAL(15, 2), nullable=True)
    actual_cost = Column(DECIMAL(15, 2), default=0)
    status = Column(Enum(ProjectStatus), default=ProjectStatus.PLANNING)
    priority = Column(Enum(ProjectPriority), default=ProjectPriority.MEDIUM)
    location = Column(String(255), nullable=True)
    contract_number = Column(String(100), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    client = relationship("Client", back_populates="projects")
    manager = relationship("User", foreign_keys=[manager_id], back_populates="managed_projects")
    tasks = relationship("ProjectTask", back_populates="project", cascade="all, delete-orphan")
    materials = relationship("ProjectMaterial", back_populates="project", cascade="all, delete-orphan")
    estimates = relationship("Estimate", back_populates="project", cascade="all, delete-orphan")
    time_entries = relationship("TimeTracking", back_populates="project")
    invoices = relationship("Invoice", back_populates="project", cascade="all, delete-orphan")
    equipment_usage = relationship("ProjectEquipment", back_populates="project", cascade="all, delete-orphan")
    documents = relationship("Document", back_populates="project", cascade="all, delete-orphan")
    
    def __repr__(self):
        return f"<Project(id={self.id}, name='{self.name}', status='{self.status}')>"


class ProjectTask(Base):
    __tablename__ = "project_tasks"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=False)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    assigned_to = Column(Integer, ForeignKey("users.id"), nullable=True)
    start_date = Column(Date, nullable=True)
    due_date = Column(Date, nullable=True)
    status = Column(Enum(TaskStatus), default=TaskStatus.PENDING)
    priority = Column(Enum(ProjectPriority), default=ProjectPriority.MEDIUM)
    progress = Column(Integer, default=0)
    estimated_hours = Column(DECIMAL(8, 2), nullable=True)
    actual_hours = Column(DECIMAL(8, 2), default=0)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="tasks")
    assigned_user = relationship("User", foreign_keys=[assigned_to], back_populates="assigned_tasks")
    time_entries = relationship("TimeTracking", back_populates="task")
    
    def __repr__(self):
        return f"<ProjectTask(id={self.id}, name='{self.name}', status='{self.status}')>"