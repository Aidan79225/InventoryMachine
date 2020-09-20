package com.aidan.inventoryworkplatform.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aidan.inventoryworkplatform.Entity.WordName

@Dao
interface WordNameDAO {
    @Query("SELECT * FROM word_name_table")
    fun getAll(): List<WordName>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordName: WordName)

    @Query("DELETE FROM word_name_table")
    fun deleteAll()

}