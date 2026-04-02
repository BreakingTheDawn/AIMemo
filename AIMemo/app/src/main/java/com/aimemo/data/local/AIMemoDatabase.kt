package com.aimemo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aimemo.data.dao.ScheduleDao
import com.aimemo.data.model.ScheduleEntity

/**
 * Room数据库抽象类
 * 包含日程表的数据访问对象
 */
@Database(
    entities = [ScheduleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AIMemoDatabase : RoomDatabase() {

    /**
     * 获取日程数据访问对象
     */
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        private const val DATABASE_NAME = "ai_memo_database"

        @Volatile
        private var INSTANCE: AIMemoDatabase? = null

        /**
         * 从版本1迁移到版本2
         * 添加提醒相关字段：reminderEnabled, reminderTime, reminderMinutesBefore
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加提醒相关字段
                database.execSQL("ALTER TABLE schedules ADD COLUMN reminderEnabled INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE schedules ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE schedules ADD COLUMN reminderMinutesBefore INTEGER NOT NULL DEFAULT 15")
            }
        }

        /**
         * 获取数据库单例实例
         * 使用双重检查锁定确保线程安全
         */
        fun getInstance(context: Context): AIMemoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AIMemoDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
