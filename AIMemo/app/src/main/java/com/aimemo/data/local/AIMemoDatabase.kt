package com.aimemo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aimemo.data.dao.ScheduleDao
import com.aimemo.data.model.ScheduleEntity

/**
 * Room数据库抽象类
 * 包含日程表的数据访问对象
 */
@Database(
    entities = [ScheduleEntity::class],
    version = 1,
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
