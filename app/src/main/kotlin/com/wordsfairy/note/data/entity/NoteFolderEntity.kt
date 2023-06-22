package com.wordsfairy.note.data.entity

import android.os.Parcelable
import androidx.room.*
import com.wordsfairy.note.data.room.NoteFolder_TableName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 21:19
 */

@Parcelize
@Entity(tableName = NoteFolder_TableName)
data class NoteFolderEntity(
    var name: String,
    val noteCount: Int,
    val createdAt: Long,
    //顺序
    var position: Int,
    //笔记数
    @ColumnInfo(name = "note_context_count")
    var noteContextCount: Int,
    //置顶
    @ColumnInfo(name = "is_topping")
    val isTopping: Boolean,
    //删除
    @ColumnInfo(name = "is_delete")
    var isDelete: Boolean
) : Parcelable ,Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var folderId: Long = 0

    companion object {
        fun create(
            name: String,
            createdAt: Long,
            position: Int = 0,
        ):NoteFolderEntity {
            return NoteFolderEntity(
                name,
                0,createdAt,
                position,
                0,
                isTopping=false,
                isDelete=false,
            )
        }
    }

}

