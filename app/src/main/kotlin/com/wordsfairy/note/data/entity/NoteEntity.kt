package com.wordsfairy.note.data.entity

import android.os.Parcelable
import androidx.room.*
import com.wordsfairy.note.data.room.NoteTableName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 21:50
 */
@kotlinx.serialization.Serializable
@Parcelize
@Entity(
    tableName = NoteTableName,
//    foreignKeys = [
//        ForeignKey(entity = NoteFolderEntity::class, parentColumns = ["id"], childColumns = ["folder_id"])
//    ],
//    indices = [Index("folder_id")]
)
data class NoteEntity(
    @ColumnInfo(name = "folder_id")
    var folderId: Long,
    var title: String,
    @ColumnInfo(name = "note_Context_count")
    var noteContextCount: Int,
    val createdAt: Long,
    var updateAt: Long,
    //置顶
    @ColumnInfo(name = "is_topping")
    val isTopping: Boolean,
    //删除
    @ColumnInfo(name = "is_delete")
    var isDelete: Boolean
) : Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var noteId: Long = 0

    companion object {
        fun create(
            folderId: Long,
            title: String,
            noteContextCount: Int,
            createdAt: Long
        ): NoteEntity {
            return NoteEntity(
                folderId,
                title, noteContextCount,
                createdAt,
                createdAt,
                isTopping = false,
                isDelete = false
            )
        }
    }

}
