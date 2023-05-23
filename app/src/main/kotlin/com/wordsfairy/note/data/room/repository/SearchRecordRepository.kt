package com.wordsfairy.note.data.room.repository

import com.wordsfairy.note.data.room.dao.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 23:35
 */


@Singleton
class SearchRecordRepository @Inject constructor(
    private val searchRecordDao: SearchRecordEntityDao
) {

    fun getRecentRecords() = searchRecordDao.getRecentRecords()

}