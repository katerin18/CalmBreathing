CREATE OR REPLACE VIEW user_latest_measurements_view AS
SELECT
    u.id AS user_id,
    u.email,
    u.first_name,
    u.last_name,
    u.enabled,
    u.email_verified,
    m.id AS measurement_id,
    m.start_pulse,
    m.exercise_duration_seconds,
    m.end_pulse,
    m.measured_at,
    m.created_at AS measurement_created_at
FROM users u
LEFT JOIN LATERAL (
    SELECT
        meas.id,
        meas.start_pulse,
        meas.exercise_duration_seconds,
        meas.end_pulse,
        meas.measured_at,
        meas.created_at
    FROM measurements meas
    WHERE meas.user_id = u.id
    ORDER BY meas.measured_at DESC, meas.created_at DESC, meas.id DESC
    LIMIT 1
) m ON TRUE;