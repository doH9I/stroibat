package com.construction.management.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.construction.management.data.model.Project
import com.construction.management.data.model.ProjectStatus
import com.construction.management.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()
    
    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadProjects()
    }
    
    fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                projectRepository.getAllProjects().collect { projects ->
                    _projects.value = projects
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectProject(project: Project?) {
        _selectedProject.value = project
    }
    
    fun createProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.insertProject(project)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.updateProject(project)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateProjectStatus(projectId: Long, status: ProjectStatus) {
        viewModelScope.launch {
            try {
                projectRepository.updateProjectStatus(projectId, status)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun deleteProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(project)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}