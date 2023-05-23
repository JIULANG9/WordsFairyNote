package com.wordsfairy.note.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.SearchRecordEntity
import com.wordsfairy.note.data.room.NoteTableName
import com.wordsfairy.note.data.room.SearchRecordName
import kotlinx.coroutines.flow.Flow


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/1/5 19:55
 */
@Dao
interface SearchRecordEntityDao {

    @Query("SELECT * FROM $SearchRecordName ORDER BY timestamp DESC LIMIT 10")
    fun getRecentRecords(): Flow<List<SearchRecordEntity>>

    @Query("DELETE FROM $SearchRecordName WHERE id NOT IN (SELECT id FROM  $SearchRecordName ORDER BY timestamp DESC LIMIT 10)")
    fun deleteOldestRecords()

}
