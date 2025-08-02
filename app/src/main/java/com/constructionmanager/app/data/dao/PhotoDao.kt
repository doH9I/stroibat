package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Photo
import java.util.Date

@Dao
interface PhotoDao {
    
    @Query("SELECT * FROM photos ORDER BY taken_at DESC")
    fun getAllPhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): Photo?
    
    @Query("SELECT * FROM photos WHERE project_id = :projectId ORDER BY taken_at DESC")
    fun getPhotosByProject(projectId: Long): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE album_name = :albumName ORDER BY taken_at DESC")
    fun getPhotosByAlbum(albumName: String): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE work_stage = :workStage ORDER BY taken_at DESC")
    fun getPhotosByWorkStage(workStage: String): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE taken_by = :userId ORDER BY taken_at DESC")
    fun getPhotosByUser(userId: Long): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE taken_at BETWEEN :startDate AND :endDate ORDER BY taken_at DESC")
    fun getPhotosByDateRange(startDate: Date, endDate: Date): Flow<List<Photo>>
    
    @Query("SELECT DISTINCT album_name FROM photos WHERE album_name IS NOT NULL ORDER BY album_name")
    fun getAllAlbums(): Flow<List<String>>
    
    @Insert
    suspend fun insertPhoto(photo: Photo): Long
    
    @Update
    suspend fun updatePhoto(photo: Photo)
    
    @Delete
    suspend fun deletePhoto(photo: Photo)
    
    @Query("SELECT COUNT(*) FROM photos WHERE project_id = :projectId")
    suspend fun getPhotosCountByProject(projectId: Long): Int
}