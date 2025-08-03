from pydantic import BaseModel, EmailStr
from typing import Optional


class ClientCreate(BaseModel):
    company_name: Optional[str] = None
    contact_person: str
    email: EmailStr
    phone: Optional[str] = None
    address: Optional[str] = None
    tax_number: Optional[str] = None
    bank_details: Optional[str] = None
    notes: Optional[str] = None
    status: str = "active"


class ClientUpdate(BaseModel):
    company_name: Optional[str] = None
    contact_person: Optional[str] = None
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    address: Optional[str] = None
    tax_number: Optional[str] = None
    bank_details: Optional[str] = None
    notes: Optional[str] = None
    status: Optional[str] = None


class ClientResponse(BaseModel):
    id: int
    company_name: Optional[str]
    contact_person: str
    email: str
    phone: Optional[str]
    address: Optional[str]
    tax_number: Optional[str]
    bank_details: Optional[str]
    notes: Optional[str]
    status: str
    display_name: str
    
    class Config:
        from_attributes = True