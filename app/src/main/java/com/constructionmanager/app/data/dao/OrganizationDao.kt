package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Organization
import com.constructionmanager.app.data.entities.OrganizationType

@Dao
interface OrganizationDao {
    
    @Query("SELECT * FROM organizations ORDER BY name")
    fun getAllOrganizations(): Flow<List<Organization>>
    
    @Query("SELECT * FROM organizations WHERE id = :id")
    suspend fun getOrganizationById(id: Long): Organization?
    
    @Query("SELECT * FROM organizations WHERE type = :type ORDER BY name")
    fun getOrganizationsByType(type: OrganizationType): Flow<List<Organization>>
    
    @Query("SELECT * FROM organizations WHERE inn = :inn")
    suspend fun getOrganizationByInn(inn: String): Organization?
    
    @Insert
    suspend fun insertOrganization(organization: Organization): Long
    
    @Update
    suspend fun updateOrganization(organization: Organization)
    
    @Delete
    suspend fun deleteOrganization(organization: Organization)
    
    @Query("SELECT COUNT(*) FROM organizations WHERE type = :type")
    suspend fun getOrganizationsCountByType(type: OrganizationType): Int
    
    @Query("SELECT * FROM organizations WHERE name LIKE '%' || :query || '%' OR full_name LIKE '%' || :query || '%'")
    fun searchOrganizations(query: String): Flow<List<Organization>>
}