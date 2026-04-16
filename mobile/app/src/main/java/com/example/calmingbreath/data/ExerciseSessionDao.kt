package com.example.calmingbreath.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseSessionDao {
    @Insert
    suspend fun insertStartData(data: ExerciseSessionEntity): Long // returns insertion id

    @Query("""
            UPDATE exercise_db
            SET bpm_after = :bpmAfter, exercise_duration = :exerciseDuration, start_exercise_time = :startExerciseTime
            WHERE id = :id
        """
    )
    suspend fun addDataAfterExercises(id: Long, bpmAfter: Int, exerciseDuration: Long, startExerciseTime: Long)

    @Query("""
            SELECT * FROM exercise_db 
            ORDER BY start_exercise_time DESC LIMIT 1
         """
    )
    suspend fun getLastExerciseData(): ExerciseSessionEntity
}