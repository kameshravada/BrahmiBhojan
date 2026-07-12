CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    mobile VARCHAR(16) NOT NULL UNIQUE,
    email VARCHAR(120) UNIQUE,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    mobile_verified_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

