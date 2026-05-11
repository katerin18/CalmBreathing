CREATE TABLE IF NOT EXISTS measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    start_pulse INTEGER NOT NULL,
    exercise_duration_seconds INTEGER NOT NULL,
    end_pulse INTEGER NOT NULL,
    measured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_measurements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_measurements_user_id ON measurements(user_id);
CREATE INDEX IF NOT EXISTS idx_measurements_measured_at ON measurements(measured_at);
CREATE INDEX IF NOT EXISTS idx_measurements_user_measured_at ON measurements(user_id, measured_at DESC, created_at DESC);