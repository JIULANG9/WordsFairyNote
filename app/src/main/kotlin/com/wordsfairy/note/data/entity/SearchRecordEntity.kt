package com.wordsfairy.note.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo
import com.wordsfairy.note.data.room.SearchRecordName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


/**
 * @Description: 搜素记录
 * @Author: JIULANG
 * @Data: 2023/1/5 19:52
 */
@Entity(tableName = SearchRecordName,indices=[Index(value = ["keyword"],unique = true)])
@Parcelize
data class SearchRecordEntity(

    val keyword :String,
    val timestamp: Long
) : Parcelable{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}

