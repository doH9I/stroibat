package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "organizations")
data class Organization(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "type")
    val type: OrganizationType,
    
    @ColumnInfo(name = "inn")
    val inn: String,
    
    @ColumnInfo(name = "kpp")
    val kpp: String?,
    
    @ColumnInfo(name = "ogrn")
    val ogrn: String,
    
    @ColumnInfo(name = "address")
    val address: String,
    
    @ColumnInfo(name = "phone")
    val phone: String?,
    
    @ColumnInfo(name = "email")
    val email: String?,
    
    @ColumnInfo(name = "director")
    val director: String,
    
    @ColumnInfo(name = "accountant")
    val accountant: String?,
    
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    
    @ColumnInfo(name = "bank_account")
    val bankAccount: String?,
    
    @ColumnInfo(name = "bik")
    val bik: String?,
    
    @ColumnInfo(name = "correspondent_account")
    val correspondentAccount: String?,
    
    @ColumnInfo(name = "rating")
    val rating: Float = 0f,
    
    @ColumnInfo(name = "payment_terms")
    val paymentTerms: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)

enum class OrganizationType {
    CLIENT,
    CONTRACTOR,
    SUPPLIER
}