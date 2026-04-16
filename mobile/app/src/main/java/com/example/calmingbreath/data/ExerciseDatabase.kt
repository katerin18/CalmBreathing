package com.example.calmingbreath.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseSessionEntity::class], version = 1)
abstract class ExerciseDatabase: RoomDatabase() {
    abstract fun exerciseDao(): ExerciseSessionDao

    companion object {
        @Volatile
        private var Instance: ExerciseDatabase? = null


        fun getDatabase(context: Context): ExerciseDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ExerciseDatabase::class.java, "exercise_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}