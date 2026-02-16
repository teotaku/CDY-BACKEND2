CREATE TABLE IF NOT EXIST study (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, --식별 pk
    title VARCHAR(255) NOT NULL, --제목
    content text NOT NULL, --내용
    user_id BIGINT NOT NULL, --작성자 식별 외래키
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, --작성날짜
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, --수정날짜

    CONSTRAINT fk_study_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
);

CREATE TABLE IF NOT EXIST study_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, --식별 pk
    imagekey VARCHAR(255), --이미지 키
    sort_order INT, --이미지 노출 순서
    study_id BIGINT NOT NULL,

    CONSTRAINT fk_study_image
    FOREIGN KEY (study_id)
    REFERENCES study(id)

    CONSTRAINT uk_study_sortorder
    UNIQUE (sort_order,study_id)
);