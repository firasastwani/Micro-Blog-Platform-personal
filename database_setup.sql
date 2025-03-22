-- Create the database.
create database if not exists cs4370_mb_platform;

-- Use the created database.
use cs4370_mb_platform;

-- Create the user table.
create table if not exists user (
    userId int auto_increment,
    username varchar(255) not null,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    primary key (userId),
    unique (username),
    constraint userName_min_length check (char_length(trim(userName)) >= 2),
    constraint firstName_min_length check (char_length(trim(firstName)) >= 2),
    constraint lastName_min_length check (char_length(trim(lastName)) >= 2)
);

--Create post table
CREATE TABLE post (
    postId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    postDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    heartsCount INT DEFAULT 0,
    commentsCount INT DEFAULT 0,
    isHearted TINYINT(1) DEFAULT 0,
    isBookmarked TINYINT(1) DEFAULT 0,
    FOREIGN KEY (userId) REFERENCES user(userId)  -- Assuming there's a 'users' table
);
--Create table comment
CREATE TABLE comment (
    commentId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    postId INT NOT NULL,
    userId INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    commentDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (postId) REFERENCES post(postId),
    FOREIGN KEY (userId) REFERENCES user(userId)
);

CREATE TABLE heart (
    postId INT not null, 
    userId INT not null, 
    PRIMARY KEY (postId, userId),
    FOREIGN KEY (postId) REFERENCES post(postId),
    FOREIGN KEY (userId) REFERENCES user(userId)
);

CREATE TABLE bookmark (
    postId INT not null, 
    userId INT not null, 
    PRIMARY KEY (postId, userId),
    FOREIGN KEY (postId) REFERENCES post(postId),
    FOREIGN KEY (userId) REFERENCES user(userId)
);

CREATE TABLE hashtag (

    hashtag varchar(255) not null, 
    postId INT not null, 
    PRIMARY KEY(hashtag, postId),
    FOREIGN KEY (postId) REFERENCES post(postID)
); 

CREATE TABLE follow (

    followerUserId INT not null, 
    followeeUserId INT not null, 
    PRIMARY KEY (followerUserId, followeeUserId),
    FOREIGN KEY (followerUserId) REFERENCES user(userId),
    FOREIGN KEY (followeeUserId) REFERENCES user(userId)
);

---create table comment
CREATE TABLE comment (
    commentId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    postId INT NOT NULL,
    userId INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    commentDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (postId) REFERENCES post(postId),
    FOREIGN KEY (userId) REFERENCES user(userId)
);
