package com.wordsfairy.note.data.room.dao

import androidx.room.*
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.room.NoteTableName
import kotlinx.coroutines.flow.Flow

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 22:23
 */
@Dao
interface NoteEntityDao {

    @Query("SELECT * FROM $NoteTableName")
    fun getAll(): Flow<List<NoteEntity>>


    @Query("SELECT * FROM $NoteTableName WHERE (folder_id = :folderId and is_delete = 0) ORDER BY is_topping DESC")
    fun getNotesByFolderId(folderId: Long): Flow<List<NoteEntity>>

    @Insert
    fun insert(noteEntity: NoteEntity): Long

    @Update
    fun update(noteEntity: NoteEntity): Int

}