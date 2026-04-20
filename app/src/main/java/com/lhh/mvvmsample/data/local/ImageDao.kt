package com.lhh.mvvmsample.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM images ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ImageEntity>>

    @Query("SELECT COUNT(*) FROM images")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<ImageEntity>)

    @Query("DELETE FROM images")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(images: List<ImageEntity>) {
        clear()
        insertAll(images)
    }
}
