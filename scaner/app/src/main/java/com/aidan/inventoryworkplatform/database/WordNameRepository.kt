package com.aidan.inventoryworkplatform.database

import com.aidan.inventoryworkplatform.Entity.WordName

class WordNameRepository {
    val wordNameDAO = AppDatabase.getInstance().getWordNameDAO()

    fun getAllWordName(): List<WordName> {
        return wordNameDAO.getAll()
    }

    fun deleteAll() {
        wordNameDAO.deleteAll()
    }
}