package com.constructionmanager.app.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.constructionmanager.app.data.entities.Project
import com.constructionmanager.app.data.entities.ProjectStatus
import com.constructionmanager.app.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()
    
    private var allProjects: List<Project> = emptyList()
    
    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                projectRepository.getAllProjects().collect { projects ->
                    allProjects = projects
                    filterProjects()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Ошибка загрузки проектов"
                )
            }
        }
    }
    
    fun filterByStatus(status: ProjectStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        filterProjects()
    }
    
    private fun filterProjects() {
        val filteredProjects = if (_uiState.value.selectedStatus == null) {
            allProjects
        } else {
            allProjects.filter { it.status == _uiState.value.selectedStatus }
        }
        
        _uiState.value = _uiState.value.copy(projects = filteredProjects)
    }
    
    fun addProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.insertProject(project)
                loadProjects() // Перезагружаем список
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Ошибка создания проекта"
                )
            }
        }
    }
    
    fun searchProjects(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                filterProjects()
            } else {
                projectRepository.searchProjects(query).collect { projects ->
                    _uiState.value = _uiState.value.copy(projects = projects)
                }
            }
        }
    }
}

data class ProjectsUiState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val selectedStatus: ProjectStatus? = null,
    val errorMessage: String? = null
)