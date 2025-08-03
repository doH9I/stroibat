package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "organizations")
@Parcelize
data class Organization(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: OrganizationType,
    val inn: String,
    val kpp: String? = null,
    val ogrn: String? = null,
    val address: String,
    val phone: String,
    val email: String,
    val website: String? = null,
    val directorName: String? = null,
    val directorPhone: String? = null,
    val directorEmail: String? = null,
    val bankName: String? = null,
    val bankAccount: String? = null,
    val bik: String? = null,
    val correspondentAccount: String? = null,
    val logoUrl: String? = null,
    val rating: Double = 0.0,
    val specialization: String? = null,
    val paymentTerms: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : Parcelable

enum class OrganizationType(val displayName: String) {
    CLIENT("Заказчик"),
    CONTRACTOR("Подрядчик"),
    SUPPLIER("Поставщик"),
    CONSTRUCTION_COMPANY("Строительная компания")
}