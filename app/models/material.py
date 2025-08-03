from sqlalchemy import Column, Integer, String, DateTime, Text, DECIMAL, ForeignKey, Date
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class Material(Base):
    __tablename__ = "materials"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    unit = Column(String(50), nullable=False)
    price_per_unit = Column(DECIMAL(10, 2), nullable=False)
    supplier = Column(String(100), nullable=True)
    supplier_contact = Column(String(100), nullable=True)
    min_stock = Column(Integer, default=0)
    current_stock = Column(Integer, default=0)
    category = Column(String(100), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    project_usage = relationship("ProjectMaterial", back_populates="material")
    
    def __repr__(self):
        return f"<Material(id={self.id}, name='{self.name}', unit='{self.unit}')>"


class ProjectMaterial(Base):
    __tablename__ = "project_materials"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=False)
    material_id = Column(Integer, ForeignKey("materials.id"), nullable=False)
    quantity = Column(DECIMAL(10, 2), nullable=False)
    unit_price = Column(DECIMAL(10, 2), nullable=False)
    total_price = Column(DECIMAL(15, 2), nullable=False)
    date_used = Column(Date, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="materials")
    material = relationship("Material", back_populates="project_usage")
    
    def __repr__(self):
        return f"<ProjectMaterial(id={self.id}, project_id={self.project_id}, material_id={self.material_id})>"