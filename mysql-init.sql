SET NAMES 'utf8mb4'; # 이렇게 해야 datagrip 과 mysql 사이에 인코딩 오류가 사라짐
CREATE DATABASE IF NOT EXISTS clone_database;
use clone_database;
drop table if Exists post_category;
create table  if not exists post_category
(
    id        bigint auto_increment
    primary key,
    level     int          not null,
    `key`     varchar(255) not null,
    parent_id bigint       null,
    kor       varchar(255) null,
    message   varchar(255) null,
    constraint UKstx3j6viwuqkmhnlf5wq096os
    unique (`key`, level),
    constraint FKmq1hp15hc97vp5tcvenjcrnlv
    foreign key (parent_id) references post_category (id)
    );

INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (2, 0, 'EVENT', null, '이벤트', '금병영에 상의하세요');
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (1, 0, 'TOPIC', null, '나라', '용건만 간단히, 움짤은 한 번 더 생각');
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (3, 0, 'SUPPORT', null, '행정실', '야생의 이벤트가 열렸다');
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (4, 1, 'SUPPORT', 3, '행정실', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (5, 1, 'EVENT', 2, '이벤트', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (6, 2, 'PHOTO', 4, '사진첩', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (7, 2, 'APPLY_TOPIC', 4, '게시판 요청', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (8, 2, 'APPLY_EMOJI', 4, '이모티콘 공모', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (9, 2, 'APPLY_STORE', 4, '팝업 신청 게시판', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (10, 2, 'APPLY_CAREER', 4, '침투부 지원하기', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (11, 2, 'APPLY_BANNER', 4, '침하하 두들', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (12, 2, 'REPORT', 4, '신고 / 건의', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (13, 1, 'CEO', 1, '침착맨', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (14, 1, 'BEST', 1, '웃음', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (15, 1, 'SPORTS', 1, '스포츠', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (16, 1, 'HOBBY', 1, '취미', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (17, 1, 'CEO_HOBBY', 1, '인방', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (18, 1, 'LIFE', 1, '일상(익명)', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (19, 1, 'GOODS', 1, '구쭈', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (20, 2, 'NOTICE', 13, '방송일정 및 공지', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (21, 2, 'CEO', 13, '침착맨', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (22, 2, 'FAKE_CEO', 13, '독깨팔', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (23, 2, 'TEMP_NOTICE', 13, '팝업 게시판', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (24, 2, 'CEO_IMAGES', 13, '침착맨 짤', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (25, 2, 'USER_DRAWS_CEO', 13, '침착맨 팬아트', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (26, 2, 'APPLY_STREAMING', 13, '방송 해줘요', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (27, 2, 'USER_RECOMENDATION', 13, '추천 침투부 & 찾아요', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (28, 2, 'APPLY_SHORTS', 13, '쇼츠 만들어줘요', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (29, 2, 'APPLY_MUSIC', 13, '음악방송 신청곡', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (30, 2, 'CEO_SELF_DRAWS', 13, '침착맨의 그림', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (31, 2, 'BEST_ARTICLE', 14, '알렉산드리아 짤 도서관', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (32, 2, 'BEST_IMAGES', 14, '짤', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (33, 2, 'BETTER_ARTICLE', 14, '유머', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (34, 2, 'GOOD_ARTICLE', 14, '호들갑', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (35, 2, 'ELDER_ARTICLE', 14, '아재개그', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (36, 2, 'STORY', 14, '이야기 & 썰', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (37, 2, 'SOCCER', 15, '축구', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (38, 2, 'BASEBALL', 15, '야구', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (39, 2, 'BASKETBALL', 15, '농구', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (40, 2, 'ETC_SPORTS', 15, '기타스포츠', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (41, 2, 'HOBBY', 16, '취미', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (42, 2, 'PET', 16, '반려동물', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (43, 2, 'FOOD', 16, '음식&여행', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (44, 2, 'IDOL', 16, '아이돌', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (45, 2, 'MUSIC', 16, '음악', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (46, 2, 'GAME', 16, '게임', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (47, 2, 'FITNESS', 16, '운동&다이어트', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (48, 2, 'MOVIE', 16, '영화&드라마', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (49, 2, 'BEAUTY', 16, '패션&뷰티', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (50, 2, 'LOL', 16, '리그 오브 레전드', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (51, 2, 'ANIME', 16, '애니&만화', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (52, 2, 'PLANT', 16, '식물', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (53, 2, 'SCIENCE', 16, 'IT&과학', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (54, 2, 'STREAMING', 17, '인터넷방송', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (55, 2, 'VTUBER', 17, '버튜버', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (56, 2, 'CEO_GOODS', 19, 'ㅊㅊㅁ구쭈', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (57, 2, 'EXT_STORE_LINK', 19, '얼렁뚱땅 상점', null);
INSERT INTO clone_database.post_category (id, level, `key`, parent_id, kor, message) VALUES (58, 2, 'REVIEW', 19, '구쭈 후기', null);
