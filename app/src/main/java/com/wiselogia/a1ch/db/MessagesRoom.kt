package com.wiselogia.a1ch.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.wiselogia.a1ch.models.UsefulMessageModel
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        UsefulMessageModel::class
   ],
    version = 1
)
abstract class MessagesRoom : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
}

@Dao
interface MessagesDao {
    @Query("SELECT * FROM messages")
    fun getAll() : Flow<List<UsefulMessageModel>>

    @Insert(onConflict = REPLACE)
    fun addAll(update: List<UsefulMessageModel>)
}