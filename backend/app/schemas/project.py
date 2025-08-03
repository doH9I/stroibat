from pydantic import BaseModel
from typing import Optional
from datetime import date
from decimal import Decimal


class ProjectCreate(BaseModel):
    name: str
    description: Optional[str] = None
    client_id: Optional[int] = None
    manager_id: Optional[int] = None
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    budget: Optional[Decimal] = None
    status: str = "planning"
    priority: str = "medium"
    location: Optional[str] = None
    contract_number: Optional[str] = None


class ProjectUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    client_id: Optional[int] = None
    manager_id: Optional[int] = None
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    budget: Optional[Decimal] = None
    status: Optional[str] = None
    priority: Optional[str] = None
    location: Optional[str] = None
    contract_number: Optional[str] = None


class ProjectResponse(BaseModel):
    id: int
    name: str
    description: Optional[str]
    client_id: Optional[int]
    manager_id: Optional[int]
    start_date: Optional[date]
    end_date: Optional[date]
    budget: Optional[Decimal]
    actual_cost: Decimal
    status: str
    priority: str
    location: Optional[str]
    contract_number: Optional[str]
    
    # Related data
    client_name: Optional[str] = None
    manager_name: Optional[str] = None
    
    class Config:
        from_attributes = True