package com.wordsfairy.note.data.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/7 22:35
 */

@Parcelize
data class NoteInfo(
    @Embedded val noteFolder: NoteFolderEntity,
    @Relation(
        entity = NoteEntity::class,
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val noteAndNoteContents: List<NoteAndNoteContent>
) : Parcelable

@Parcelize
data class NoteAndFolder(
    @Embedded val noteFolder: NoteFolderEntity,
    @Relation(
        entity = NoteEntity::class,
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val notes: List<NoteEntity> ?= null
) : Parcelable

