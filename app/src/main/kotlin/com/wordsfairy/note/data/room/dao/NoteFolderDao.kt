package com.wordsfairy.note.data.room.dao

import androidx.room.*
import com.wordsfairy.note.data.entity.*
import com.wordsfairy.note.data.room.NoteContentTableName
import com.wordsfairy.note.data.room.NoteFolder_TableName
import kotlinx.coroutines.flow.Flow

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 22:13
 */
@Dao
interface NoteFolderDao {

    @Query("SELECT * FROM $NoteFolder_TableName")
    fun getAllFolder(): Flow<List<NoteFolderEntity>>

    @Query("SELECT * FROM $NoteFolder_TableName WHERE is_delete = 0 ORDER BY position ASC")
    fun getNoteFolder(): Flow<List<NoteFolderEntity>>

    @Query("SELECT * FROM $NoteFolder_TableName WHERE id = :id")
    fun getNoteFolderById(id:Long): NoteFolderEntity

    @Query("SELECT MAX(position) FROM $NoteFolder_TableName")
    fun getMaxPosition(): Int
    @Insert
    suspend fun insert(noteFolderEntity: NoteFolderEntity): Long
    @Delete
    fun delete(noteFolderEntity: NoteFolderEntity)

    @Update
    fun update(noteFolder: NoteFolderEntity): Int
    @Update
    fun update(noteFolder: List<NoteFolderEntity>): Int
}