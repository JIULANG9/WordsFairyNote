package com.wordsfairy.note.data.room.repository

import androidx.room.Insert
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.room.dao.NoteContentDao
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/9 22:18
 */
@Singleton
class NoteContentRepository @Inject constructor(
    private val noteContentDao: NoteContentDao,
) {
    fun getNoteContexts(noteId : Long) : Flow<List<NoteContentEntity>> = noteContentDao.getAllByNoteIdFlow(noteId)


    fun searchContent(list: List<NoteContentEntity>, keyword: String): List<NoteContentEntity> {
        val result = mutableListOf<NoteContentEntity>()
        for (note in list) {
            if (note.content.contains(keyword)) {
                result.add(note)
            }
        }
        return result
    }
    /**
     * 查询笔记最大位置 同于排序
     * @param noteId Long
     * @return Int
     */
    fun getMaxPosition(noteId: Long) = noteContentDao.getMaxPosition(noteId)

    /**
     *
     * @param noteId Long
     * @return Long
     */
    fun getRecentUpdates(noteId: Long) = noteContentDao.getRecentUpdates(noteId)

    suspend fun createNoteContentEntity(noteContent: NoteContentEntity)= noteContentDao.insert(noteContent)

    fun update(data: NoteContentEntity) = noteContentDao.update(data)

    fun update(data: List<NoteContentEntity>) = noteContentDao.update(data)
    suspend fun insert(data: NoteContentEntity) = noteContentDao.insert(data)
    suspend fun insert(data: List<NoteContentEntity>) = noteContentDao.insertNotes(data)

    fun recycleAll(noteId: Long) = noteContentDao.recycleNoteContents(noteId)

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }
}