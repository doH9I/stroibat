from sqlalchemy import Column, Integer, String, DateTime, Text, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class Document(Base):
    __tablename__ = "documents"
    
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=True)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    file_path = Column(String(500), nullable=False)
    file_type = Column(String(100), nullable=True)
    file_size = Column(Integer, nullable=True)
    uploaded_by = Column(Integer, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    # Relationships
    project = relationship("Project", back_populates="documents")
    uploader = relationship("User", foreign_keys=[uploaded_by], back_populates="uploaded_documents")
    
    def __repr__(self):
        return f"<Document(id={self.id}, name='{self.name}', file_type='{self.file_type}')>"