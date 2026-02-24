CREATE TABLE IF NOT EXISTS users(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_number(50) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_category VARCHAR(50) NOT NULL,
    role VARCHAR(50),
    description TEXT,
    nickname VARCHAR(50) NOT NULL,
    profile_image_key VARCHAR(255),
    is_deleted TINYINT(1) DEFAULT 0


)