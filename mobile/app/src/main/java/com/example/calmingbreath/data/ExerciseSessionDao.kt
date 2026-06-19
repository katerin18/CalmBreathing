package com.example.calmingbreath.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSessionDao {
    @Insert
    suspend fun insertStartData(data: ExerciseSessionEntity): Long // returns insertion id

    @Query("""
            UPDATE exercise_db
            SET bpm_after = :bpmAfter, exercise_duration = :exerciseDuration, start_exercise_time = :startExerciseTime, completed = 1
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

    @Query("UPDATE exercise_db SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    // Завершённые, но ещё не отправленные на бэкенд замеры текущего пользователя.
    @Query("SELECT * FROM exercise_db WHERE synced = 0 AND completed = 1 AND user_id = :userId")
    suspend fun getUnsynced(userId: String): List<ExerciseSessionEntity>

    // Вся завершённая история замеров, свежие сверху.
    @Query("SELECT * FROM exercise_db WHERE completed = 1 ORDER BY start_exercise_time DESC")
    fun getHistory(): Flow<List<ExerciseSessionEntity>>
}