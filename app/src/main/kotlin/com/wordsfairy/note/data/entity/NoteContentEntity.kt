package com.wordsfairy.note.data.entity

import android.os.Parcelable
import androidx.room.*
import com.wordsfairy.note.data.room.NoteContentTableName
import com.wordsfairy.note.data.room.NoteTableName
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 22:01
 */

@Parcelize
@Entity(
    tableName = NoteContentTableName,
    foreignKeys = [
        ForeignKey(entity = NoteEntity::class, parentColumns = ["id"], childColumns = ["note_id"]),
    ],
    indices = [Index("note_id")]
)
data class NoteContentEntity(
    @ColumnInfo(name = "note_id")
    var noteId: Long,
    var content: String,
    val createdAt: Long,
    val updateAt: Long,
    //顺序
    @ColumnInfo(name = "position")
    var position: Int,
    //置顶
    @ColumnInfo(name = "is_topping")
    val isTopping: Boolean,
    //删除
    @ColumnInfo(name = "is_delete")
    var isDelete: Boolean,
    @ColumnInfo(name = "is_complete")
    val isComplete: Boolean
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var noteContextId: Long = 0

    companion object {
        fun create(
            noteId: Long,
            context: String,
            createdAt: Long,
            position: Int = 0,
        ): NoteContentEntity {
            return NoteContentEntity(
                noteId,
                context,
                createdAt,
                createdAt,
                position,
                isTopping = false,
                isDelete = false,
                isComplete = false
            )
        }
    }
}


@Parcelize
@DatabaseView
data class NoteAndNoteContent(
    @Embedded
    val noteEntity: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_id"
    )
    val noteContents: List<NoteContentEntity>
) : Parcelable

@Parcelize
data class SearchNoteEntity(
    val folderName: String? = null,
    @Embedded
    val noteEntity: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_id"
    )
    val noteContents: List<NoteContentEntity>
): Parcelable
