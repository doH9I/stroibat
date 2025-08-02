package com.constructionmanager.app.ui.screens.estimates

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.constructionmanager.app.data.entities.Estimate
import com.constructionmanager.app.data.entities.EstimateStatus
import com.constructionmanager.app.data.repository.EstimateRepository
import com.constructionmanager.app.utils.ExportUtils
import com.constructionmanager.app.utils.EstimateExportItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EstimatesViewModel @Inject constructor(
    private val estimateRepository: EstimateRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EstimatesUiState())
    val uiState: StateFlow<EstimatesUiState> = _uiState.asStateFlow()
    
    private var allEstimates: List<Estimate> = emptyList()
    
    fun loadEstimates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                estimateRepository.getAllEstimates().collect { estimates ->
                    allEstimates = estimates
                    filterEstimates()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Ошибка загрузки смет: ${e.message}"
                )
            }
        }
    }
    
    fun filterByStatus(status: EstimateStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        filterEstimates()
    }
    
    private fun filterEstimates() {
        val filteredEstimates = if (_uiState.value.selectedStatus == null) {
            allEstimates
        } else {
            allEstimates.filter { it.status == _uiState.value.selectedStatus }
        }
        
        _uiState.value = _uiState.value.copy(estimates = filteredEstimates)
    }
    
    fun createEstimate(name: String, projectId: Long) {
        viewModelScope.launch {
            try {
                val estimate = Estimate(
                    projectId = projectId,
                    name = name,
                    version = 1,
                    totalAmount = BigDecimal.ZERO,
                    vatRate = BigDecimal("0.20"), // 20% НДС
                    vatAmount = BigDecimal.ZERO,
                    totalWithVat = BigDecimal.ZERO,
                    overheadRate = BigDecimal("0.15"), // 15% накладные расходы
                    contingencyRate = BigDecimal("0.05"), // 5% непредвиденные расходы
                    status = EstimateStatus.DRAFT,
                    createdBy = 1L, // TODO: Получить ID текущего пользователя
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                estimateRepository.insertEstimate(estimate)
                _uiState.value = _uiState.value.copy(message = "Смета создана успешно")
                loadEstimates()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Ошибка создания сметы: ${e.message}"
                )
            }
        }
    }
    
    fun exportEstimateToPdf(context: Context, estimate: Estimate) {
        viewModelScope.launch {
            try {
                // Создаем тестовые данные для экспорта
                val items = createSampleEstimateItems()
                
                val result = ExportUtils.exportEstimateToPdf(
                    context = context,
                    estimateName = estimate.name,
                    items = items,
                    totalAmount = estimate.totalAmount.toDouble(),
                    vatAmount = estimate.vatAmount.toDouble(),
                    totalWithVat = estimate.totalWithVat.toDouble()
                )
                
                result.fold(
                    onSuccess = { filePath ->
                        _uiState.value = _uiState.value.copy(
                            message = "PDF экспортирован: $filePath"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            message = "Ошибка экспорта PDF: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Ошибка экспорта: ${e.message}"
                )
            }
        }
    }
    
    fun exportEstimateToCsv(context: Context, estimate: Estimate) {
        viewModelScope.launch {
            try {
                // Создаем тестовые данные для экспорта
                val items = createSampleEstimateItems()
                
                val result = ExportUtils.exportEstimateToCsv(
                    context = context,
                    estimateName = estimate.name,
                    items = items,
                    totalAmount = estimate.totalAmount.toDouble(),
                    vatAmount = estimate.vatAmount.toDouble(),
                    totalWithVat = estimate.totalWithVat.toDouble()
                )
                
                result.fold(
                    onSuccess = { filePath ->
                        _uiState.value = _uiState.value.copy(
                            message = "CSV экспортирован: $filePath"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            message = "Ошибка экспорта Excel: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Ошибка экспорта: ${e.message}"
                )
            }
        }
    }
    
    private fun createSampleEstimateItems(): List<EstimateExportItem> {
        return listOf(
            EstimateExportItem(
                description = "Земляные работы",
                unit = "м³",
                quantity = 100.0,
                unitPrice = 500.0,
                totalPrice = 50000.0
            ),
            EstimateExportItem(
                description = "Устройство фундамента",
                unit = "м³",
                quantity = 50.0,
                unitPrice = 3000.0,
                totalPrice = 150000.0
            ),
            EstimateExportItem(
                description = "Кладка стен",
                unit = "м²",
                quantity = 200.0,
                unitPrice = 1500.0,
                totalPrice = 300000.0
            ),
            EstimateExportItem(
                description = "Устройство кровли",
                unit = "м²",
                quantity = 150.0,
                unitPrice = 2000.0,
                totalPrice = 300000.0
            )
        )
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class EstimatesUiState(
    val isLoading: Boolean = false,
    val estimates: List<Estimate> = emptyList(),
    val selectedStatus: EstimateStatus? = null,
    val message: String? = null
)