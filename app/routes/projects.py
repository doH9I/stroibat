from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.auth import get_current_active_user, require_manager_or_admin
from app.models.user import User
from app.models.project import Project
from app.models.client import Client
from app.schemas.project import ProjectCreate, ProjectUpdate, ProjectResponse

router = APIRouter(prefix="/projects", tags=["projects"])


@router.get("/", response_model=List[ProjectResponse])
async def get_projects(
    skip: int = 0,
    limit: int = 100,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Get list of projects."""
    query = db.query(Project).offset(skip).limit(limit)
    
    # If user is not admin/manager, show only their projects
    if current_user.role not in ["admin", "manager"]:
        query = query.filter(Project.manager_id == current_user.id)
    
    projects = query.all()
    
    # Add related data
    result = []
    for project in projects:
        project_dict = {
            **project.__dict__,
            "client_name": project.client.display_name if project.client else None,
            "manager_name": project.manager.full_name if project.manager else None
        }
        result.append(project_dict)
    
    return result


@router.get("/{project_id}", response_model=ProjectResponse)
async def get_project(
    project_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Get project by ID."""
    project = db.query(Project).filter(Project.id == project_id).first()
    
    if not project:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Project not found"
        )
    
    # Check permissions
    if (current_user.role not in ["admin", "manager"] and 
        project.manager_id != current_user.id):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not enough permissions"
        )
    
    return {
        **project.__dict__,
        "client_name": project.client.display_name if project.client else None,
        "manager_name": project.manager.full_name if project.manager else None
    }


@router.post("/", response_model=ProjectResponse)
async def create_project(
    project_data: ProjectCreate,
    current_user: User = Depends(require_manager_or_admin),
    db: Session = Depends(get_db)
):
    """Create new project."""
    # Validate client exists if provided
    if project_data.client_id:
        client = db.query(Client).filter(Client.id == project_data.client_id).first()
        if not client:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Client not found"
            )
    
    # Validate manager exists if provided
    if project_data.manager_id:
        manager = db.query(User).filter(User.id == project_data.manager_id).first()
        if not manager:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Manager not found"
            )
    
    # Create project
    project = Project(**project_data.dict())
    db.add(project)
    db.commit()
    db.refresh(project)
    
    return {
        **project.__dict__,
        "client_name": project.client.display_name if project.client else None,
        "manager_name": project.manager.full_name if project.manager else None
    }


@router.put("/{project_id}", response_model=ProjectResponse)
async def update_project(
    project_id: int,
    project_data: ProjectUpdate,
    current_user: User = Depends(require_manager_or_admin),
    db: Session = Depends(get_db)
):
    """Update project."""
    project = db.query(Project).filter(Project.id == project_id).first()
    
    if not project:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Project not found"
        )
    
    # Update project fields
    for field, value in project_data.dict(exclude_unset=True).items():
        setattr(project, field, value)
    
    db.commit()
    db.refresh(project)
    
    return {
        **project.__dict__,
        "client_name": project.client.display_name if project.client else None,
        "manager_name": project.manager.full_name if project.manager else None
    }


@router.delete("/{project_id}")
async def delete_project(
    project_id: int,
    current_user: User = Depends(require_manager_or_admin),
    db: Session = Depends(get_db)
):
    """Delete project."""
    project = db.query(Project).filter(Project.id == project_id).first()
    
    if not project:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Project not found"
        )
    
    db.delete(project)
    db.commit()
    
    return {"message": "Project deleted successfully"}