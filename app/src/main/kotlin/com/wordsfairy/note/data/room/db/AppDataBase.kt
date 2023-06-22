package com.wordsfairy.note.data.room.db

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wordsfairy.note.base.BaseApplication
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.SearchRecordEntity
import com.wordsfairy.note.data.room.NoteContentTableName
import com.wordsfairy.note.data.room.NoteFolder_TableName
import com.wordsfairy.note.data.room.dao.*
import com.wordsfairy.note.data.room.db.SeedDatabaseWorker.Companion.KEY_FILENAME
import com.wordsfairy.note.data.room.db.SeedDatabaseWorker.Companion.NoteFolder_Name
import java.util.*

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 18:09
 */

@Database(
    entities = [
        NoteFolderEntity::class,
        NoteContentEntity::class,
        NoteEntity::class,
        SearchRecordEntity::class,
    ], version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun noteFolderDao(): NoteFolderDao

    abstract fun noteContentDao(): NoteContentDao

    abstract fun noteEntityDao(): NoteEntityDao

    abstract fun searchRecordDao(): SearchRecordEntityDao

    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "note_db"
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase().also { instance = it }
            }
        }

        private fun buildDatabase(): AppDataBase {
            return Room.databaseBuilder(BaseApplication.CONTEXT, AppDataBase::class.java, DATABASE_NAME)
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                .setInputData(workDataOf(KEY_FILENAME to NoteFolder_Name))
                                .build()
                            WorkManager.getInstance(BaseApplication.CONTEXT).enqueue(request)
                        }
                    }
                )
                .addMigrations(migration_1_2) // 添加 Migration 对象
                .build()
        }
        private val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 在 note_folder_entity 表中将order字段改名为position
                database.execSQL("ALTER TABLE $NoteFolder_TableName RENAME COLUMN `order` TO `position`")
            }
        }
        val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 在 note_folder_entity 表中添加新字段
                database.execSQL("ALTER TABLE $NoteFolder_TableName ADD COLUMN `note_context_count` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

}
class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
