package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GroupSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupSettingsDao {
    @Query("SELECT * FROM group_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<GroupSettingsEntity?>

    @Query("SELECT * FROM group_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): GroupSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: GroupSettingsEntity)

    @Update
    suspend fun update(settings: GroupSettingsEntity)
}
