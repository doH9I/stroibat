package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["taken_by"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "file_path")
    val filePath: String,
    
    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String?,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double?,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double?,
    
    @ColumnInfo(name = "album_name")
    val albumName: String?,
    
    @ColumnInfo(name = "work_stage")
    val workStage: String?,
    
    @ColumnInfo(name = "tags")
    val tags: String?, // JSON array of tags
    
    @ColumnInfo(name = "taken_by")
    val takenBy: Long,
    
    @ColumnInfo(name = "taken_at")
    val takenAt: Date = Date(),
    
    @ColumnInfo(name = "file_size")
    val fileSize: Long,
    
    @ColumnInfo(name = "width")
    val width: Int,
    
    @ColumnInfo(name = "height")
    val height: Int
)