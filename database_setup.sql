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
    FOREIGN KEY (userId) REFERENCES users(userId)  -- Assuming there's a 'users' table
);

