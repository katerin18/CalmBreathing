package com.example.calmingbreath.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_db")
data class ExerciseSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("bpm_before") val bpmBefore: Int = 0,
    @ColumnInfo("bpm_after") val bpmAfter: Int = 0,
    @ColumnInfo("exercise_duration") val exercisesDurationSec: Long = 0L,
    @ColumnInfo("start_exercise_time") val startExerciseTime: Long = 0L,
    @ColumnInfo("completed") val completed: Boolean = false,
    @ColumnInfo("synced") val synced: Boolean = false
)