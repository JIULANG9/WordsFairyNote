package com.wordsfairy.note.data.room.dao

import androidx.room.*
import com.wordsfairy.note.data.entity.NoteContentEntity

import com.wordsfairy.note.data.room.NoteContentTableName

import kotlinx.coroutines.flow.Flow

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 22:38
 */
@Dao
interface NoteContentDao {

    @Query("SELECT * FROM $NoteContentTableName WHERE (note_id = :noteId and is_delete = 0) ORDER BY is_topping DESC,is_complete ASC,position DESC")
    fun getAllByNoteIdFlow(noteId: Long): Flow<List<NoteContentEntity>>


    @Query("SELECT MAX(position) FROM $NoteContentTableName WHERE note_id =:noteId")
    fun getMaxPosition(noteId:Long): Int

    /** 最近更新 */
    @Query("SELECT MAX(updateAt) FROM $NoteContentTableName WHERE note_id =:noteId")
    fun getRecentUpdates(noteId:Long): Long

    @Insert
    suspend fun insert(noteContent: NoteContentEntity): Long

    @Insert
    fun insertNotes(notes: List<NoteContentEntity>): List<Long>

    @Update
    fun update(noteContentEntity: NoteContentEntity): Int

    @Update
    fun update(noteContents: List<NoteContentEntity>): Int

    @Query("UPDATE $NoteContentTableName SET is_delete = 1 WHERE note_id = :noteId")
    fun recycleNoteContents(noteId: Long)
}