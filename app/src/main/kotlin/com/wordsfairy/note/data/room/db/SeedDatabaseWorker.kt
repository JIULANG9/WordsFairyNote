/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wordsfairy.note.data.room.db

import android.content.Context
import android.util.JsonReader
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SeedDatabaseWorker(
        context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val createdAt = System.currentTimeMillis()
            val noteFolder = NoteFolderEntity.create(
                NoteFolder_Name,
                createdAt,
                position = 1
            )
            val database = AppDataBase.getInstance()
           val folderId = database.noteFolderDao().insert(noteFolder)

            val note = NoteEntity.create(
                folderId,
                "词语仙境",
                0,
                createdAt,
            )
            val noteId = database.noteEntityDao().insert(note)
            val contents = listOf(
                NoteContentEntity.create(noteId,"词语仙境里,新奇的词汇如同不知名的花朵,散发着智慧的芬芳。",createdAt,position = 1),
                NoteContentEntity.create(noteId,"翻开词典这本通往词语仙境的魔法之书,无数生动的词语在脑海中喷涌而出。",createdAt,position = 2),
                NoteContentEntity.create(noteId,"词语仙境的住客,是那些初次闯入者的知识盲区,等待着被发现与理解。 ",createdAt,position = 3),
                NoteContentEntity.create(noteId,"词义的海洋中游荡,就如同在词语仙境的广阔疆域里穿行。",createdAt,position = 4),
                NoteContentEntity.create(noteId,"每个新掌握的词语,都将词语仙境的疆域扩展一分,让智慧之门开启一线。",createdAt,position = 5),
            )
            database.noteContentDao().insertNotes(contents)

            Result.success()

        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_FILENAME = "PLANT_DATA_FILENAME"
        const val NoteFolder_Name = "笔记"
    }
}
