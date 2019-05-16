insert into role (id, role_name) values (1, 'USER');
insert into role (id, role_name) values (2, 'ADMIN');

insert into user (id,  email, nickname, passwd) values (1, 'mimi@naver.com', '미미', '{bcrypt}$2a$10$iSjd8nYlZitpdiadODvsouFExJGWVuhnLLFOAHLkZ7QiuiO7gzop6');
insert into user (id,  email, nickname, passwd) values (2, 'test@naver.com', '테스트', '{bcrypt}$2a$10$qhp3futbDLSulrDi.X9wiO3alWNU5ZnEAtuGy7mN2DiyJXVfmrp72');

insert into user_role (user_id, role_id) values (1, 1);
insert into user_role (user_id, role_id) values (1, 2);
insert into user_role (user_id, role_id) values (2, 1);

insert into keyword (id, keyword_type, keyword_value, ordering) values (1, 1, '일', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (2, 1, '월', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (3, 1, '화', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (4, 1, '수', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (5, 1, '목', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (6, 1, '금', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (7, 1, '토', 0);

insert into keyword (id, keyword_type, keyword_value, ordering) values (8, 2, '드라마', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (9, 2, '순정', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (10, 2, '코믹', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (11, 2, '일상', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (12, 2, '판타지', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (13, 2, '액션', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (14, 2, '공포', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (15, 2, '스릴러', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (16, 2, '스포츠', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (17, 2, '시대물', 0);

insert into keyword (id, keyword_type, keyword_value, ordering) values (18, 4, '역사', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (19, 4, '싸움', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (20, 4, '좀비', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (21, 4, '타임리프', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (22, 4, '여자들', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (23, 4, '연애', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (24, 4, '개그', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (25, 4, '대학생', 1);
insert into keyword (id, keyword_type, keyword_value, ordering) values (26, 4, '학교', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (27, 4, '범죄', 0);
insert into keyword (id, keyword_type, keyword_value, ordering) values (28, 4, '귀신', 0);

insert into platform (id, platform_name) values (1, 'Naver');
insert into platform (id, platform_name) values (2, 'Daum');
insert into platform (id, platform_name) values (3, 'Lezhin');