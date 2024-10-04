INSERT INTO role (role_name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_USER');

INSERT INTO role (role_name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_ADMIN');

INSERT INTO role (role_name)
SELECT 'ROLE_MANAGER'
    WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_MANAGER');

-- 더미 데이터
INSERT INTO users (deleted, created_at, deleted_at, updated_at, email, nickname, password, picture, provider)
VALUES
    (false, '2023-01-15 09:15:23.123456', null, '2023-01-15 09:15:23.123456', 'kim.minsoo@example.com', '민수kim', '{bcrypt}$2a$10$abcdefghijklmnopqrstuvwxyz123456789', null, 'LOCAL'),
    (false, '2023-02-22 10:30:45.678901', null, '2023-02-22 10:30:45.678901', 'park.jiyoung@gmail.com', '지영팍', null, 'https://example.com/jiyoung.jpg', 'GOOGLE'),
    (false, '2023-03-07 11:45:56.789012', null, '2023-03-07 11:45:56.789012', 'lee.sungmin@naver.com', '성민이', null, null, 'NAVER'),
    (true, '2023-04-30 14:20:30.246810', '2024-01-15 09:10:25.135790', '2024-01-15 09:10:25.135790', 'choi.yuna@example.com', '유나최', '{bcrypt}$2a$10$zyxwvutsrqponmlkjihgfedcba987654321', null, 'LOCAL'),
    (false, '2023-05-18 16:55:40.369258', null, '2023-05-18 16:55:40.369258', 'hong.gildong@gmail.com', '길동홍', null, 'https://example.com/gildong.jpg', 'GOOGLE'),
    (false, '2023-06-25 08:30:15.951753', null, '2023-06-25 08:30:15.951753', 'kang.sora@naver.com', '소라강', null, null, 'NAVER'),
    (false, '2023-07-14 12:45:30.159357', null, '2023-07-14 12:45:30.159357', 'jung.minho@example.com', '민호정', '{bcrypt}$2a$10$qponmlkjihgfedcbazyxwvutsrq987654321', null, 'LOCAL'),
    (true, '2023-08-09 17:20:45.753159', '2024-02-20 10:15:30.951357', '2024-02-20 10:15:30.951357', 'shin.yeonjoo@gmail.com', '연주신', null, 'https://example.com/yeonjoo.jpg', 'GOOGLE'),
    (false, '2023-09-03 09:10:20.357951', null, '2023-09-03 09:10:20.357951', 'kwon.jiae@naver.com', '지애권', null, null, 'NAVER'),
    (false, '2023-10-11 14:55:35.753159', null, '2023-10-11 14:55:35.753159', 'yoo.jaesuk@example.com', '재석유', '{bcrypt}$2a$10$lkjihgfedcbazyxwvutsrqponm123456789', null, 'LOCAL'),
    (false, '2023-11-28 11:25:50.159357', null, '2023-11-28 11:25:50.159357', 'song.joongki@gmail.com', '중기송', null, 'https://example.com/joongki.jpg', 'GOOGLE'),
    (true, '2023-12-17 13:40:05.951753', '2024-03-05 09:30:15.357951', '2024-03-05 09:30:15.357951', 'kim.taehee@naver.com', '태희킴', null, null, 'NAVER'),
    (false, '2024-01-05 10:05:25.753159', null, '2024-01-05 10:05:25.753159', 'lee.minho@example.com', '민호이', '{bcrypt}$2a$10$jihgfedcbazyxwvutsrqponmlk987654321', null, 'LOCAL'),
    (false, '2024-02-14 15:50:40.357951', null, '2024-02-14 15:50:40.357951', 'park.bogum@gmail.com', '보검팍', null, 'https://example.com/bogum.jpg', 'GOOGLE'),
    (false, '2024-03-22 08:15:55.159357', null, '2024-03-22 08:15:55.159357', 'han.hyojoo@naver.com', '효주한', null, null, 'NAVER'),
    (false, '2023-01-30 11:20:30.246810', null, '2023-01-30 11:20:30.246810', 'jo.insung@example.com', '인성조', '{bcrypt}$2a$10$abcdefghijklmnopqrstuvwxyz987654321', null, 'LOCAL'),
    (false, '2023-03-15 13:45:40.369258', null, '2023-03-15 13:45:40.369258', 'bae.suzy@gmail.com', '수지배', null, 'https://example.com/suzy.jpg', 'GOOGLE'),
    (true, '2023-05-02 09:30:15.951753', '2024-01-20 14:25:30.159357', '2024-01-20 14:25:30.159357', 'gong.yoo@naver.com', '유공', null, null, 'NAVER'),
    (false, '2023-06-18 16:55:30.159357', null, '2023-06-18 16:55:30.159357', 'son.yejin@example.com', '예진손', '{bcrypt}$2a$10$zyxwvutsrqponmlkjihgfedcba123456789', null, 'LOCAL'),
    (false, '2023-08-24 10:10:45.753159', null, '2023-08-24 10:10:45.753159', 'kim.soohyun@gmail.com', '수현킴', null, 'https://example.com/soohyun.jpg', 'GOOGLE'),
    (false, '2022-10-07 14:35:20.357951', null, '2022-10-07 14:35:20.357951', 'jun.jihyun@naver.com', '지현전', null, null, 'NAVER'),
    (false, '2023-11-19 08:50:35.753159', null, '2023-11-19 08:50:35.753159', 'ha.jungwoo@example.com', '정우하', '{bcrypt}$2a$10$qponmlkjihgfedcbazyxwvutsrq123456789', null, 'LOCAL'),
    (true, '2024-01-01 12:15:50.159357', '2024-03-10 11:40:25.753159', '2024-03-10 11:40:25.753159', 'lee.jongsuk@gmail.com', '종석이', null, 'https://example.com/jongsuk.jpg', 'GOOGLE'),
    (false, '2024-02-28 15:40:05.951753', null, '2024-02-28 15:40:05.951753', 'kim.goeun@naver.com', '고은킴', null, null, 'NAVER'),
    (false, '2023-04-12 09:25:25.753159', null, '2023-04-12 09:25:25.753159', 'jang.keunseok@example.com', '근석장', '{bcrypt}$2a$10$lkjihgfedcbazyxwvutsrqponm987654321', null, 'LOCAL'),
    (false, '2023-07-05 14:50:40.357951', null, '2023-07-05 14:50:40.357951', 'im.yoona@gmail.com', '윤아임', null, 'https://example.com/yoona.jpg', 'GOOGLE'),
    (false, '2023-09-20 11:15:55.159357', null, '2023-09-20 11:15:55.159357', 'park.seojoon@naver.com', '서준팍', null, null, 'NAVER'),
    (true, '2023-12-03 16:40:30.246810', '2024-02-25 13:55:45.753159', '2024-02-25 13:55:45.753159', 'oh.yeonseo@example.com', '연서오', '{bcrypt}$2a$10$jihgfedcbazyxwvutsrqponmlk123456789', null, 'LOCAL'),
    (false, '2024-03-18 10:05:40.369258', null, '2024-03-18 10:05:40.369258', 'jung.haein@gmail.com', '해인정', null, 'https://example.com/haein.jpg', 'GOOGLE');

-- 사용자 역할
INSERT INTO user_role (role_id, user_id)
VALUES
-- 1. kim.minsoo@example.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'kim.minsoo@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'kim.minsoo@example.com')),

-- 2. park.jiyoung@gmail.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'park.jiyoung@gmail.com')),

-- 3. lee.sungmin@naver.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'lee.sungmin@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'lee.sungmin@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'lee.sungmin@naver.com')),

-- 4. choi.yuna@example.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'choi.yuna@example.com')),

-- 5. hong.gildong@gmail.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'hong.gildong@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'hong.gildong@gmail.com')),

-- 6. kang.sora@naver.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'kang.sora@naver.com')),

-- 7. jung.minho@example.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'jung.minho@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'jung.minho@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'jung.minho@example.com')),

-- 8. shin.yeonjoo@gmail.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'shin.yeonjoo@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'shin.yeonjoo@gmail.com')),

-- 9. kwon.jiae@naver.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'kwon.jiae@naver.com')),

-- 10. yoo.jaesuk@example.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'yoo.jaesuk@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'yoo.jaesuk@example.com')),

-- 11. song.joongki@gmail.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'song.joongki@gmail.com')),

-- 12. kim.taehee@naver.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'kim.taehee@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'kim.taehee@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'kim.taehee@naver.com')),

-- 13. lee.minho@example.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'lee.minho@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'lee.minho@example.com')),

-- 14. park.bogum@gmail.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'park.bogum@gmail.com')),

-- 15. han.hyojoo@naver.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'han.hyojoo@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'han.hyojoo@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'han.hyojoo@naver.com')),

-- 16. jo.insung@example.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'jo.insung@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'jo.insung@example.com')),

-- 17. bae.suzy@gmail.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'bae.suzy@gmail.com')),

-- 18. gong.yoo@naver.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'gong.yoo@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'gong.yoo@naver.com')),

-- 19. son.yejin@example.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'son.yejin@example.com')),

-- 20. kim.soohyun@gmail.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'kim.soohyun@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'kim.soohyun@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'kim.soohyun@gmail.com')),

-- 21. jun.jihyun@naver.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'jun.jihyun@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'jun.jihyun@naver.com')),

-- 22. ha.jungwoo@example.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'ha.jungwoo@example.com')),

-- 23. lee.jongsuk@gmail.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'lee.jongsuk@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'lee.jongsuk@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'lee.jongsuk@gmail.com')),

-- 24. kim.goeun@naver.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'kim.goeun@naver.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'kim.goeun@naver.com')),

-- 25. jang.keunseok@example.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'jang.keunseok@example.com')),

-- 26. im.yoona@gmail.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'im.yoona@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'im.yoona@gmail.com')),

-- 27. park.seojoon@naver.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'park.seojoon@naver.com')),

-- 28. oh.yeonseo@example.com (3개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'oh.yeonseo@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'oh.yeonseo@example.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_ADMIN'),
 (SELECT user_id FROM users WHERE email = 'oh.yeonseo@example.com')),

-- 29. jung.haein@gmail.com (2개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'jung.haein@gmail.com')),
((SELECT role_id FROM role WHERE role_name = 'ROLE_MANAGER'),
 (SELECT user_id FROM users WHERE email = 'jung.haein@gmail.com')),

-- 30. last.user@example.com (1개 권한)
((SELECT role_id FROM role WHERE role_name = 'ROLE_USER'),
 (SELECT user_id FROM users WHERE email = 'last.user@example.com'));