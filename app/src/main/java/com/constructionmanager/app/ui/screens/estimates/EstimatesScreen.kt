package com.constructionmanager.app.ui.screens.estimates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.constructionmanager.app.data.entities.Estimate
import com.constructionmanager.app.data.entities.EstimateStatus

import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatesScreen(
    viewModel: EstimatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadEstimates()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок и кнопка добавления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сметы",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Создать смету"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Фильтры по статусу
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            item {
                FilterChip(
                    onClick = { viewModel.filterByStatus(null) },
                    label = { Text("Все") },
                    selected = uiState.selectedStatus == null
                )
            }
            items(EstimateStatus.entries) { status ->
                FilterChip(
                    onClick = { viewModel.filterByStatus(status) },
                    label = { Text(getStatusDisplayName(status)) },
                    selected = uiState.selectedStatus == status
                )
            }
        }
        
        // Список смет
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.estimates.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет смет",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.estimates) { estimate ->
                    EstimateCard(
                        estimate = estimate,
                        onExportPdf = {
                            viewModel.exportEstimateToPdf(context, estimate)
                        },
                        onExportExcel = {
                            viewModel.exportEstimateToExcel(context, estimate)
                        },
                        onClick = { /* TODO: Navigate to estimate details */ }
                    )
                }
            }
        }
    }
    
    // Диалог создания сметы
    if (showAddDialog) {
        AddEstimateDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, projectId ->
                viewModel.createEstimate(name, projectId)
                showAddDialog = false
            }
        )
    }
    
    // Показываем сообщения об ошибках или успехе
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            // Здесь можно показать Snackbar
        }
    }
}

@Composable
fun EstimateCard(
    estimate: Estimate,
    onExportPdf: () -> Unit,
    onExportExcel: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = estimate.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Версия ${estimate.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                EstimateStatusChip(status = estimate.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Сумма без НДС",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(estimate.totalAmount.toDouble()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Итого с НДС",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(estimate.totalWithVat.toDouble()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Кнопки экспорта
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExportPdf,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
                
                OutlinedButton(
                    onClick = onExportExcel,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel")
                }
            }
        }
    }
}

@Composable
fun EstimateStatusChip(status: EstimateStatus) {
    val (color, text) = when (status) {
        EstimateStatus.DRAFT -> MaterialTheme.colorScheme.outline to "Черновик"
        EstimateStatus.PENDING_APPROVAL -> MaterialTheme.colorScheme.secondary to "На согласовании"
        EstimateStatus.APPROVED -> MaterialTheme.colorScheme.tertiary to "Утверждена"
        EstimateStatus.REJECTED -> MaterialTheme.colorScheme.error to "Отклонена"
        EstimateStatus.ARCHIVED -> MaterialTheme.colorScheme.outline to "Архив"
    }
    
    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.12f),
            labelColor = color
        )
    )
}

@Composable
fun AddEstimateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var projectId by remember { mutableStateOf("1") } // TODO: Выбор проекта из списка
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая смета") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название сметы") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = projectId,
                    onValueChange = { projectId = it },
                    label = { Text("ID проекта") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && projectId.isNotBlank()) {
                        onConfirm(name, projectId.toLongOrNull() ?: 1L)
                    }
                }
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private fun getStatusDisplayName(status: EstimateStatus): String {
    return when (status) {
        EstimateStatus.DRAFT -> "Черновик"
        EstimateStatus.PENDING_APPROVAL -> "На согласовании"
        EstimateStatus.APPROVED -> "Утверждена"
        EstimateStatus.REJECTED -> "Отклонена"
        EstimateStatus.ARCHIVED -> "Архив"
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)
}