CREATE TABLE IF NOT EXISTS users(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_category VARCHAR(50) NOT NULL,
    description TEXT,
    nickname VARCHAR(50) NOT NULL,
    profile_image_key VARCHAR(255),
    is_deleted TINYINT(1) DEFAULT 0


)