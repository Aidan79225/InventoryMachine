package com.aidan.inventoryworkplatform.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_name_table")
data class WordName(
        @PrimaryKey
        val id: String,
        @ColumnInfo
        val name: String
)