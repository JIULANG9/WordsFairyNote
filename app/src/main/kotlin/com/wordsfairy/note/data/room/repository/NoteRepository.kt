package com.wordsfairy.note.data.room.repository

import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.entity.NoteInfo
import com.wordsfairy.note.data.entity.SearchNoteEntity
import com.wordsfairy.note.data.room.dao.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 23:03
 */
@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
) {


    /**
     * 首页
     * @return List<NoteInfo>
     */
    fun getHomeNoteInfo(): Flow<List<NoteInfo>> {
        return noteDao.getAllNoteInfo().map { noteInfoList ->
            val allNotes = noteDao.getHomeNoteAndNoteContents().map { noteAndNoteContent ->
                noteAndNoteContent.copy(
                    noteContents = noteAndNoteContent.noteContents.filter { !it.isDelete }.takeLast(5).reversed()
                )
            }
            val allNoteInfo = NoteInfo(
                NoteFolderEntity.create(name = "全部",0L),
                allNotes
            )
            listOf(allNoteInfo) + noteInfoList.map { noteInfo ->
                noteInfo.copy(
                    noteAndNoteContents = noteInfo.noteAndNoteContents.map { noteAndNoteContent ->
                        noteAndNoteContent.copy(
                            noteContents = noteAndNoteContent.noteContents.filter { !it.isDelete }.takeLast(5).reversed()
                        )
                    }
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 所有数据据
     * @return List<NoteInfo>
     */
    fun getAllNoteInfo(): Flow<List<NoteInfo>> {
        return noteDao.getAllNoteInfo().map { noteInfoList ->

            val unclassifiedNotes = noteDao.getHomeNoteAndNoteContents().filter{ it.noteEntity.folderId ==0L }.map { noteAndNoteContent ->
                noteAndNoteContent.copy(
                    noteContents = noteAndNoteContent.noteContents.filter { !it.isDelete }.reversed()
                )
            }

            val allNoteInfo = NoteInfo(
                NoteFolderEntity.create(name = "未分类",0L),
                unclassifiedNotes
            )
            listOf(allNoteInfo)
            noteInfoList.map { noteInfo ->
                noteInfo.copy(
                    noteAndNoteContents = noteInfo.noteAndNoteContents.map { noteAndNoteContent ->
                        noteAndNoteContent.copy(
                            noteContents = noteAndNoteContent.noteContents.filter { !it.isDelete }.reversed()
                        )
                    }
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 搜索
     * @return List<SearchNoteEntity>
     */
    suspend fun getSearchNotes(): List<SearchNoteEntity> {
        return noteDao.getNotesWithNameAndContent()
    }

    /**
     主要的优化点有:
    1. 使用HashSet代替ArrayList存储folderName和noteTitle,避免重复和加快查找速度。
    2. 使用HashMap代替ArrayList存储noteContents,key为noteId加快查找速度。
    3. 先分别检查folderName、noteTitle和noteContents,过滤出包含关键词的项。
    4. 筛选后的项可能重复,使用distinctBy过滤重复项。
    5. 其余优化:例如it.noteContents.getOrPut { } 代替 if-else 语句检查noteContents等。
    这个优化后的实现通过HashSet、HashMap和预先过滤的方式,减少了重复项的检查和添加,优化了性能。
    同时保留了结果的正确性。
    但仍可能存在例如结果排序的问题,并且如果参数变化频繁,HashSet和HashMap的性能优势可能减弱。
     * @param keyword String
     * @param noteList List<SearchNoteEntity>
     * @return List<SearchNoteEntity>
     */
    fun searchNotes(
        keyword: String,
        noteList: List<SearchNoteEntity>
    ): List<SearchNoteEntity> {
        val results = arrayListOf<SearchNoteEntity>()
        val folderNames = hashSetOf<String>()
        val noteTitles = hashSetOf<String>()
        val noteContents = hashMapOf<Long, ArrayList<NoteContentEntity>>()

        noteList.forEach {
            if (it.folderName?.contains(keyword, ignoreCase = true) == true) {
                folderNames.add(it.folderName)
            }
            if (it.noteEntity.title.contains(keyword, ignoreCase = true)) {
                noteTitles.add(it.noteEntity.title)
            }
            it.noteContents.filter { data -> !data.isDelete }.forEach { content ->
                if (content.content.contains(keyword, ignoreCase = true)) {
                    noteContents.getOrPut(it.noteEntity.noteId) { arrayListOf() }.add(content)
                }
            }
        }

        if (folderNames.isNotEmpty()) {
            results.addAll(noteList.filter { folderNames.contains(it.folderName) })
        }
        if (noteTitles.isNotEmpty()) {
            results.addAll(noteList.filter { noteTitles.contains(it.noteEntity.title) })
        }
        noteContents.forEach { (noteId, contents) ->
            val note = noteList.first { it.noteEntity.noteId == noteId }
            results.add(SearchNoteEntity(note.folderName, note.noteEntity, contents))
        }

        return results.distinctBy { it.noteEntity.noteId }
    }

    //查询所有数据


    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: NoteRepository? = null

        fun getInstance(
            noteDao: NoteDao,
        ) =
            instance ?: synchronized(this) {
                instance ?: NoteRepository(
                    noteDao
                ).also { instance = it }
            }
    }
}