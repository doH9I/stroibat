package com.constructionmanager.app.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.UnitValue
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    
    /**
     * Экспорт данных в PDF
     */
    fun exportToPdf(
        context: Context,
        title: String,
        headers: List<String>,
        data: List<List<String>>,
        fileName: String = "export_${System.currentTimeMillis()}.pdf"
    ): Result<String> {
        return try {
            val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            
            val file = File(documentsDir, fileName)
            val writer = PdfWriter(file)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc)
            
            // Заголовок документа
            document.add(
                Paragraph(title)
                    .setFontSize(18f)
                    .setBold()
            )
            
            document.add(
                Paragraph("Дата создания: ${dateFormat.format(Date())}")
                    .setFontSize(10f)
                    .setMarginBottom(20f)
            )
            
            // Создаем таблицу
            val table = Table(UnitValue.createPercentArray(headers.size))
            table.setWidth(UnitValue.createPercentValue(100f))
            
            // Добавляем заголовки
            headers.forEach { header ->
                table.addHeaderCell(
                    Paragraph(header)
                        .setBold()
                        .setFontSize(12f)
                )
            }
            
            // Добавляем данные
            data.forEach { row ->
                row.forEach { cell ->
                    table.addCell(
                        Paragraph(cell)
                            .setFontSize(10f)
                    )
                }
            }
            
            document.add(table)
            document.close()
            
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Экспорт данных в Excel
     */
    fun exportToExcel(
        context: Context,
        title: String,
        headers: List<String>,
        data: List<List<String>>,
        fileName: String = "export_${System.currentTimeMillis()}.xlsx"
    ): Result<String> {
        return try {
            val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            
            val file = File(documentsDir, fileName)
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet(title)
            
            var rowIndex = 0
            
            // Заголовок документа
            val titleRow = sheet.createRow(rowIndex++)
            val titleCell = titleRow.createCell(0)
            titleCell.setCellValue(title)
            
            // Дата создания
            val dateRow = sheet.createRow(rowIndex++)
            val dateCell = dateRow.createCell(0)
            dateCell.setCellValue("Дата создания: ${dateFormat.format(Date())}")
            
            // Пустая строка
            rowIndex++
            
            // Заголовки колонок
            val headerRow = sheet.createRow(rowIndex++)
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }
            
            // Данные
            data.forEach { rowData ->
                val dataRow = sheet.createRow(rowIndex++)
                rowData.forEachIndexed { index, cellValue ->
                    val cell = dataRow.createCell(index)
                    cell.setCellValue(cellValue)
                }
            }
            
            // Автоподбор ширины колонок
            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }
            
            // Сохраняем файл
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()
            
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Экспорт сметы в PDF
     */
    fun exportEstimateToPdf(
        context: Context,
        estimateName: String,
        items: List<EstimateExportItem>,
        totalAmount: Double,
        vatAmount: Double,
        totalWithVat: Double
    ): Result<String> {
        val headers = listOf("№", "Наименование работ", "Ед. изм.", "Кол-во", "Цена", "Сумма")
        val data = items.mapIndexed { index, item ->
            listOf(
                (index + 1).toString(),
                item.description,
                item.unit,
                item.quantity.toString(),
                formatCurrency(item.unitPrice),
                formatCurrency(item.totalPrice)
            )
        }.toMutableList()
        
        // Добавляем итоговые строки
        data.add(listOf("", "", "", "", "ИТОГО:", formatCurrency(totalAmount)))
        data.add(listOf("", "", "", "", "НДС:", formatCurrency(vatAmount)))
        data.add(listOf("", "", "", "", "ВСЕГО:", formatCurrency(totalWithVat)))
        
        return exportToPdf(
            context = context,
            title = "Смета: $estimateName",
            headers = headers,
            data = data,
            fileName = "estimate_${estimateName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        )
    }
    
    /**
     * Экспорт сметы в Excel
     */
    fun exportEstimateToExcel(
        context: Context,
        estimateName: String,
        items: List<EstimateExportItem>,
        totalAmount: Double,
        vatAmount: Double,
        totalWithVat: Double
    ): Result<String> {
        val headers = listOf("№", "Наименование работ", "Ед. изм.", "Кол-во", "Цена", "Сумма")
        val data = items.mapIndexed { index, item ->
            listOf(
                (index + 1).toString(),
                item.description,
                item.unit,
                item.quantity.toString(),
                formatCurrency(item.unitPrice),
                formatCurrency(item.totalPrice)
            )
        }.toMutableList()
        
        // Добавляем итоговые строки
        data.add(listOf("", "", "", "", "ИТОГО:", formatCurrency(totalAmount)))
        data.add(listOf("", "", "", "", "НДС:", formatCurrency(vatAmount)))
        data.add(listOf("", "", "", "", "ВСЕГО:", formatCurrency(totalWithVat)))
        
        return exportToExcel(
            context = context,
            title = "Смета: $estimateName",
            headers = headers,
            data = data,
            fileName = "estimate_${estimateName.replace(" ", "_")}_${System.currentTimeMillis()}.xlsx"
        )
    }
    
    private fun formatCurrency(amount: Double): String {
        return String.format("%.2f ₽", amount)
    }
}

data class EstimateExportItem(
    val description: String,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double
)