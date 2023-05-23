package com.wordsfairy.note.ui.page.detail


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wordsfairy.common.tools.timestampToString
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.ext.flow.withLatestFrom

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/9 17:10
 */


@ExperimentalCoroutinesApi
@HiltViewModel
class NoteDetailsViewModel @Inject internal constructor(
    private val folderRepository: NoteFolderRepository,
    private val noteRepository: NoteEntityRepository,
    private val contentRepository: NoteContentRepository
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    //笔记文件夹
    val noteFolders: Flow<List<NoteFolderEntity>> = folderRepository.getNoteFolder()

    var searchResultDataCache : MutableList<NoteContentEntity> = ArrayList()
    //searchResultDataCache初始长度为126,list长度超过126,addAll()无法自动扩容,导致下标越界
    var totalSize = 0
    var noteContents: (Long) -> Flow<List<NoteContentEntity>> = { id ->
        val data =contentRepository.getNoteContexts(id)

        viewModelScope.launch(Dispatchers.IO) {
            data.collect{ list ->
                totalSize += list.size
                searchResultDataCache.clear()
                //在addAll()前检查searchResultDataCache长度,并手动扩容至足够大小
                if (searchResultDataCache.size < totalSize) {
                    searchResultDataCache = ArrayList(totalSize)
                }
                searchResultDataCache.addAll(list)
            }
        }
        data
    }

    init {
        val initialVS = ViewState.initial()
        viewStateFlow = intentFlow
            .toPartialChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->
            val event = when (change) {
                is PartialChange.NoteData.SaveTitle -> SingleEvent.UI.Close
                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        shareWhileSubscribed().run {

            /** 初始化 */
            val initFlow = filterIsInstance<ViewIntent.Initial>()
                .log("[Intent]")
                .map {
                    val noteEntity = it.noteEntity
                    val folderEntity = if (noteEntity.folderId == 0L){
                        null
                    }else{
                        folderRepository.getNoteFolderById(noteEntity.folderId)
                    }
                    PartialChange.UI.Init(noteEntity,folderEntity)
                }.flowOn(Dispatchers.IO).distinctUntilChanged()


            /** clean */
            val cleanFlow = filterIsInstance<ViewIntent.Clean>()
                .log("[clean]")
                .map {
                    PartialChange.UI.Clean
                }
            /**切换状态*/
            val uiStateFlow = filterIsInstance<ViewIntent.UIStateChanged>()
                .log("[clean]")
                .map {
                    PartialChange.UI.UIStateChanged(it.uiState)
                }

            /** 最近更新时间 */
            val recentUpdatesFlow = filterIsInstance<ViewIntent.RecentUpdates>()
                .log("[最近更新时间]")
                .zip(initFlow) { _, init ->
                    val time = init.noteEntity.updateAt.timestampToString()
                    PartialChange.UI.RecentUpdates(time)
                }

            /** 移动笔记文件夹 */
            val selectFolderFlow = filterIsInstance<ViewIntent.SelectFolder>()
                .log("[移动笔记文件夹]")
                .map {
                    val note = it.selectFolder
                    //选择文件夹后 如果noteEntity已经创建则 更新笔记实体
                    val noteEntity = viewStateFlow.value.noteEntity
                    if (noteEntity != null) {
                        noteEntity.folderId = note.folderId
                        noteRepository.update(noteEntity)
                    }
                    PartialChange.NoteData.SelectFolder(note)
                }.flowOn(Dispatchers.IO)


            val titleContentFlow = filterIsInstance<ViewIntent.TitleChanged>()
                .log("[标题内容]:titleContentFlow")
                .map { it.title }

            val titleContentChangeFlow = titleContentFlow.map { title ->
                val canSave = if (viewStateFlow.value.title == title) {
                    false
                } else title.isNotEmpty()

                PartialChange.UI.Title(title, canSave)
            }.distinctUntilChanged()


            /** 修改标题 */
            val modifyTitleFlow = filterIsInstance<ViewIntent.ModifyTitle>()
                .log("[修改标题]")
                .withLatestFrom(titleContentFlow) { _, title ->
                   val noteEntity = viewStateFlow.value.noteEntity!!
                    noteEntity.title =title
                    noteRepository.update(noteEntity)
                    PartialChange.NoteData.SaveTitle
                }.flowOn(Dispatchers.IO)

            /** 笔记内容 */
            val noteContentChanges = filterIsInstance<ViewIntent.ContentChanged>()
                .log("[笔记内容]:titleContentFlow")
                .map { it.content }
                .distinctUntilChanged()
            val noteContentChangesFlow = noteContentChanges.map { noteContent ->
                val canSave = if (viewStateFlow.value.noteContent == noteContent) {
                    false
                } else noteContent.isNotEmpty()
                PartialChange.UI.Content(noteContent, canSave)
            }.distinctUntilChanged()

            /** 收集笔记内容 */
            val noteContentEntityForm = combine(
                noteContentChanges
            ) { _, noteContent ->
                NoteContentEntity.create(0L, noteContent, System.currentTimeMillis())
            }.mapNotNull { it }

            /** 添加笔记 */
            val addNoteContentFlow = filterIsInstance<ViewIntent.AddNoteContent>()
                .log("[添加笔记]")
                .withLatestFrom(noteContentEntityForm) { _, noteContent ->

                    val noteId = viewStateFlow.value.noteEntity?.noteId!!
                    //查询笔记最大位置 同于排序
                    val maxPosition = contentRepository.getMaxPosition(noteId)
                    //笔记最大位置+1
                    noteContent.noteId = noteId
                    noteContent.position = maxPosition + 1
                    contentRepository.createNoteContentEntity(noteContent)
                    //更新时间
                    noteRepository.update(viewStateFlow.value.noteEntity!!)
                    //文件夹笔记内容条数 +1
                    val selectedFolder = viewStateFlow.value.selectedFolder
                    if (selectedFolder!=null){
                        selectedFolder.noteContextCount += 1
                        folderRepository.update(selectedFolder)
                    }
                    PartialChange.NoteData.AddNoteContent
                }.flowOn(Dispatchers.IO)
            //更新笔记位置
            val movePositionFloe = filterIsInstance<ViewIntent.MovePosition>()
                .log("[更新笔记位置]")
                .map {
                    contentRepository.update(it.noteContents)
                    PartialChange.NoteData.UpDataPosition
                }
                .flowOn(Dispatchers.IO)


            /** 修改笔记内容 */
            val modifyContentChanged = filterIsInstance<ViewIntent.ModifyContentChanged>()
                .log("[修改笔记内容]")
                .map { PartialChange.NoteData.ModifyNoteContent(it.noteContentEntity) }


            val modifyContentFlow = filterIsInstance<ViewIntent.ModifyContent>()
                .log("[修改笔记内容]")
                .map {
                    contentRepository.update(it.noteContentEntity)
                    PartialChange.NoteData.UpDataNoteContent
                }
                .flowOn(Dispatchers.IO)

            /**
             * 删除笔记内容
             */
            val deleteContentFlow = filterIsInstance<ViewIntent.DeleteContent>()
                .log("[删除笔记内容]")
                .map {
                   val note = it.noteContentEntity
                    note.isDelete = true
                    contentRepository.update(note)
                    PartialChange.NoteData.DeleteContent
                }
                .flowOn(Dispatchers.IO)

            val searchFlow = filterIsInstance<ViewIntent.SearchContent>()
                .log("[搜索笔记]")
                .map {
                    val keyword = it.keyword
                    val resultData = if (keyword.isNotEmpty()){
                        contentRepository.searchContent(searchResultDataCache,keyword)
                    }else{
                        searchResultDataCache
                    }
                    PartialChange.NoteData.SearchResultData(resultData)
                }.flowOn(Dispatchers.IO)
            val closeSearchFlow = filterIsInstance<ViewIntent.InitSearch>()
                .log("[搜索笔记]")
                .map {
                    PartialChange.NoteData.InitSearch(searchResultDataCache)
                }


            return merge(
                initFlow, cleanFlow,uiStateFlow, recentUpdatesFlow,
                titleContentChangeFlow,
                modifyTitleFlow,
                noteContentChangesFlow,
                selectFolderFlow,
                movePositionFloe,
                addNoteContentFlow,
                modifyContentChanged,
                modifyContentFlow,
                deleteContentFlow,
                searchFlow,
                closeSearchFlow
            )
        }

    private companion object {
        private const val VIEW_STATE = "NoteDetailsViewModel"
    }
}