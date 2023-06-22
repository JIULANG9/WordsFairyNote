package com.wordsfairy.note.data.room.repository

import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.room.dao.NoteContentDao
import com.wordsfairy.note.data.room.dao.NoteDao
import com.wordsfairy.note.data.room.dao.NoteEntityDao
import com.wordsfairy.note.data.room.dao.NoteFolderDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/10 21:55
 */
@Singleton
class NoteFolderRepository @Inject constructor(
    private val noteFolderDao: NoteFolderDao,
) {
    fun getNoteFolder() = noteFolderDao.getNoteFolder()
    fun getNoteFolderById(id: Long) = noteFolderDao.getNoteFolderById(id)

    fun getMaxPosition() = noteFolderDao.getMaxPosition()
    suspend fun insert(noteFolder: NoteFolderEntity) = noteFolderDao.insert(noteFolder)
    suspend fun update(noteFolder: NoteFolderEntity) = noteFolderDao.update(noteFolder)
    suspend fun update(noteFolder: List<NoteFolderEntity>) = noteFolderDao.update(noteFolder)

    suspend fun delete(noteFolder: NoteFolderEntity)  {
        noteFolder.isDelete = true
        noteFolderDao.update(noteFolder)
    }


    suspend fun createNoteFolder(noteFolderEntity: NoteFolderEntity) =
        noteFolderDao.insert(noteFolderEntity)

    companion object {
        @Volatile
        private var instance: NoteFolderRepository? = null
        fun getInstance(
            noteFolderDao: NoteFolderDao,
        ) =
            instance ?: synchronized(this) {
                instance ?: NoteFolderRepository(
                    noteFolderDao
                ).also { instance = it }
            }
    }
}