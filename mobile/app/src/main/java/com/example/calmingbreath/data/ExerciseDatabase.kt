package com.example.calmingbreath.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ExerciseSessionEntity::class], version = 4)
abstract class ExerciseDatabase: RoomDatabase() {
    abstract fun exerciseDao(): ExerciseSessionDao

    companion object {
        @Volatile
        private var Instance: ExerciseDatabase? = null

        // v1 -> v2: добавлены флаги completed и synced (existing rows -> 0).
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exercise_db ADD COLUMN completed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE exercise_db ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v2 -> v3: старые завершённые сессии (есть пульс после) задним числом помечаем completed = 1,
        // чтобы они появились в истории.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE exercise_db SET completed = 1 WHERE bpm_after != 0")
            }
        }

        // v3 -> v4: привязка строки к пользователю. Старые строки получают пустой user_id
        // и не будут выгружаться ни в один аккаунт.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exercise_db ADD COLUMN user_id TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): ExerciseDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ExerciseDatabase::class.java, "exercise_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}