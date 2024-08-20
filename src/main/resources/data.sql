-- Update users
UPDATE users SET password = 'passwordA' WHERE username = 'userA';
UPDATE users SET password = 'passwordB' WHERE username = 'userB';

-- Update posts for userA
UPDATE posts SET title = 'Post 1', content = 'Content for post 1', status = 'POSTED', created_date = NOW(), body = 'Body for post 1', views = 1, category = 'ABOUT' WHERE id = 1;
UPDATE posts SET title = 'Post 2', content = 'Content for post 2', status = 'POSTED', created_date = NOW(), body = 'Body for post 2', views = 2, category = 'CHIM' WHERE id = 2;
UPDATE posts SET title = 'Post 3', content = 'Content for post 3', status = 'POSTED', created_date = NOW(), body = 'Body for post 3', views = 3, category = 'CONTACT' WHERE id = 3;
UPDATE posts SET title = 'Post 4', content = 'Content for post 4', status = 'POSTED', created_date = NOW(), body = 'Body for post 4', views = 4, category = 'HOBBY' WHERE id = 4;
UPDATE posts SET title = 'Post 5', content = 'Content for post 5', status = 'POSTED', created_date = NOW(), body = 'Body for post 5', views = 5, category = 'HOME' WHERE id = 5;

-- Update posts for userB
UPDATE posts SET title = 'Post 6', content = 'Content for post 6', status = 'POSTED', created_date = NOW(), body = 'Body for post 6', views = 0, category = 'HUMOR' WHERE id = 6;
UPDATE posts SET title = 'Post 7', content = 'Content for post 7', status = 'POSTED', created_date = NOW(), body = 'Body for post 7', views = 0, category = 'INTERNET' WHERE id = 7;
UPDATE posts SET title = 'Post 8', content = 'Content for post 8', status = 'POSTED', created_date = NOW(), body = 'Body for post 8', views = 0, category = 'LIFE' WHERE id = 8;
UPDATE posts SET title = 'Post 9', content = 'Content for post 9', status = 'POSTED', created_date = NOW(), body = 'Body for post 9', views = 0, category = 'LOGIN' WHERE id = 9;