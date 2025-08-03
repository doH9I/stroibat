package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "photos")
@Parcelize
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val description: String? = null,
    val filePath: String,
    val thumbnailPath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val takenAt: LocalDateTime,
    val uploadedBy: Long,
    val uploadedAt: LocalDateTime = LocalDateTime.now(),
    val albumId: Long? = null,
    val tags: String? = null, // JSON array of tags
    val isPublic: Boolean = false
) : Parcelable

@Entity(tableName = "photo_albums")
@Parcelize
data class PhotoAlbum(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val name: String,
    val description: String? = null,
    val coverPhotoId: Long? = null,
    val createdBy: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isPublic: Boolean = false
) : Parcelable