from sqlalchemy import Column, Integer, DateTime, Text, DECIMAL, ForeignKey, Date, Time, Boolean
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class TimeTracking(Base):
    __tablename__ = "time_tracking"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=True)
    task_id = Column(Integer, ForeignKey("project_tasks.id"), nullable=True)
    date = Column(Date, nullable=False)
    start_time = Column(Time, nullable=True)
    end_time = Column(Time, nullable=True)
    hours_worked = Column(DECIMAL(5, 2), default=0)
    description = Column(Text, nullable=True)
    is_approved = Column(Boolean, default=False)
    approved_by = Column(Integer, ForeignKey("users.id"), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    
    # Relationships
    user = relationship("User", foreign_keys=[user_id], back_populates="time_entries")
    project = relationship("Project", back_populates="time_entries")
    task = relationship("ProjectTask", back_populates="time_entries")
    approver = relationship("User", foreign_keys=[approved_by], back_populates="approved_time_entries")
    
    def __repr__(self):
        return f"<TimeTracking(id={self.id}, user_id={self.user_id}, date='{self.date}', hours={self.hours_worked})>"