package com.constructionmanager.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.constructionmanager.app.data.entities.Project
import com.constructionmanager.app.data.entities.Report
import com.constructionmanager.app.data.entities.ProjectStatus
import com.constructionmanager.app.data.repository.ProjectRepository
import com.constructionmanager.app.data.repository.ReportRepository
import com.constructionmanager.app.data.repository.UserRepository
import com.constructionmanager.app.data.repository.IssueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val issueRepository: IssueRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                // Загружаем статистику
                val activeProjectsCount = projectRepository.getProjectsCountByStatus(ProjectStatus.IN_PROGRESS)
                val openIssuesCount = issueRepository.getOpenIssuesCount()
                val activeUsersCount = userRepository.getActiveUsersCount()
                
                // Загружаем последние проекты
                val recentProjects = projectRepository.getRecentProjects(5)
                
                // Загружаем последние отчеты
                val recentReports = reportRepository.getRecentReports(5)
                
                _uiState.value = _uiState.value.copy(
                    activeProjectsCount = activeProjectsCount,
                    openIssuesCount = openIssuesCount,
                    activeUsersCount = activeUsersCount,
                    recentProjects = recentProjects,
                    recentReports = recentReports,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Ошибка загрузки данных",
                    isLoading = false
                )
            }
        }
    }
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val activeProjectsCount: Int = 0,
    val openIssuesCount: Int = 0,
    val activeUsersCount: Int = 0,
    val recentProjects: List<Project> = emptyList(),
    val recentReports: List<Report> = emptyList(),
    val errorMessage: String? = null
)