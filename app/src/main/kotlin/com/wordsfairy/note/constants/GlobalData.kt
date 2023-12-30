package com.wordsfairy.note.constants

import android.net.Uri
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity

/**
 * @Description: 全局临时缓存
 * @Author: JIULANG
 * @Data: 2023/5/10 23:07
 */
/**
 * 笔记详细
 */

object GlobalData {
   var noteDetailsNoteEntity : NoteEntity? = null
   var noteDetailsNoteFolderEntity : NoteFolderEntity? = null
   var importFile : Uri? = null

   var createBatchImport : Boolean = false

   //备份文件夹的Uri
   var backupsSelectFolderUri : Uri? = null
   var importFolderUri : Uri? = null

   //搜索的内容
   var searchContent = ""
}

