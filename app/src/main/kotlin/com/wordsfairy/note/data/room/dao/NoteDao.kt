package com.wordsfairy.note.data.room.dao

import androidx.room.*
import com.wordsfairy.note.data.entity.*
import com.wordsfairy.note.data.room.NoteContentTableName
import com.wordsfairy.note.data.room.NoteFolder_TableName
import com.wordsfairy.note.data.room.NoteTableName
import kotlinx.coroutines.flow.Flow

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/8 21:40
 */
@Dao
interface NoteDao {

    /**
     * 首页
     * @return List<NoteInfo>
     */

    @Transaction
    @Query("SELECT * FROM $NoteFolder_TableName WHERE is_delete = 0 ORDER BY position ASC")
    fun getAllNoteInfo(): Flow<List<NoteInfo>>


    @Transaction
    @Query("SELECT * FROM $NoteTableName WHERE is_delete = 0")
    suspend fun getHomeNoteAndNoteContents(): List<NoteAndNoteContent>

    @Transaction
    @Query("SELECT note_folder_entity.name as folderName, note_entity.* FROM note_entity " +
            "LEFT JOIN note_folder_entity ON note_folder_entity.id = note_entity.folder_id " +
            "WHERE note_entity.is_delete = 0 " +
            "ORDER BY note_entity.createdAt DESC")
    suspend fun getNotesWithNameAndContent(): List<SearchNoteEntity>

}