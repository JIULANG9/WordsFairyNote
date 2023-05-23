package com.wordsfairy.note.data.room.repository

import androidx.room.Update
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.room.dao.NoteEntityDao
import com.wordsfairy.note.data.room.dao.NoteFolderDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/10 22:06
 */

@Singleton
class NoteEntityRepository @Inject constructor(
    private val noteDao: NoteEntityDao,
){

    fun createNoteEntity(noteEntity: NoteEntity): Long {
        return noteDao.insert(noteEntity)
    }

    /**
     * 更新
     * @return Int
     */
    fun update(noteEntity: NoteEntity): Int  {
        noteEntity.updateAt = System.currentTimeMillis()
       return noteDao.update(noteEntity)
    }
    /**
     * 更新
     * @return Int
     */
    fun recycle(noteEntity: NoteEntity): Int  {
        noteEntity.isDelete = true
        return update(noteEntity)
    }

    companion object {
        @Volatile
        private var instance: NoteEntityRepository? = null
        fun getInstance(
            noteDao: NoteEntityDao,
        ) =
            instance ?: synchronized(this) {
                instance ?: NoteEntityRepository(
                    noteDao
                ).also { instance = it }
            }
    }
}