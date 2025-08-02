package com.constructionmanager.app.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.UnitValue
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
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
     * Экспорт данных в CSV
     */
    fun exportToCsv(
        context: Context,
        title: String,
        headers: List<String>,
        data: List<List<String>>,
        fileName: String = "export_${System.currentTimeMillis()}.csv"
    ): Result<String> {
        return try {
            val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            
            val file = File(documentsDir, fileName)
            val writer = CSVWriter(file.writer())
            
            // Заголовок документа
            writer.writeNext(arrayOf(title))
            
            // Дата создания
            writer.writeNext(arrayOf("Дата создания: ${dateFormat.format(Date())}"))
            
            // Пустая строка
            writer.writeNext(arrayOf())
            
            // Заголовки колонок
            writer.writeNext(headers.toTypedArray())
            
            // Данные
            data.forEach { rowData ->
                writer.writeNext(rowData.toTypedArray())
            }
            
            writer.close()
            
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
     * Экспорт сметы в CSV
     */
    fun exportEstimateToCsv(
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
        
        return exportToCsv(
            context = context,
            title = "Смета: $estimateName",
            headers = headers,
            data = data,
            fileName = "estimate_${estimateName.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
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