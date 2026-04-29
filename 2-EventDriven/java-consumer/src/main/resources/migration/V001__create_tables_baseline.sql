CREATE TABLE IF NOT EXISTS idems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author VARCHAR(50) NOT NULL,
    message VARCHAR(280) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_seeded BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_idems_created_at ON idems(created_at DESC);