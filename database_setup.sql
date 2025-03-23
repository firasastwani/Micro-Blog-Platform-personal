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

-- Create post table
CREATE TABLE post (
    postId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    postDate DATETIME DEFAULT CURRENT_TIMESTAMP,    
    FOREIGN KEY (userId) REFERENCES user(userId)  -- Assuming there's a 'users' table
);
-- Create table comment
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

INSERT INTO `user` (`userId`, `username`, `password`, `firstName`, `lastName`) VALUES (3,'Jdoe','$2a$10$FuSTlhix0urXrTLA8PeaferFPUPhUx4Yya1QiQgdmymb0ZsV5d9Ry','John','Doe'),(4,'LebronJames23','$2a$10$EGkssAfxMYZ/fjcv/4uU8OBNVohy7i9wyVBpoVAqAyBIUCTusMhCy','Lebron','James'),(5,'SusieM','$2a$10$p7mVW1LDjoSzLqeTIYdBKeD2SHMmegHFd2tXWe8r96deYtSdAf6y.','Susie','Mae'),(6,'TyBrown','$2a$10$IVq4kDmkTf1u9mFEP1JhRed3/e1EiNBvPn8vonBHfVLfYXStNN2CS','Tyler','Brown'),(7,'warriorsfan30','$2a$10$in4Js0jbayprVx4tJcAKcOy1eiHFxug1.sVt5wq7A2OHR00bRBgPi','Darren','Reese');

INSERT INTO `post` (`postId`, `userId`, `content`, `postDate`) VALUES (3,3,'How was everyone\'s weekend? I went to watch the Lakers game and saw Lebron!','2025-03-23 22:06:37'),(4,5,'Just got back from vacation in Italy! #vacation #europe','2025-03-23 22:09:28'),(5,4,'Big W today against the Warriors! #lakers','2025-03-23 22:10:11'),(6,6,'Anyone have any recs for places to eat in Italy? #europe','2025-03-23 22:13:21'),(7,3,'Lakers will win the championship this year, I\'m calling it!','2025-03-23 22:16:20'),(8,7,'Honestly the Lakers are kinda overrated, warriors on top! #lakers #warriors','2025-03-23 22:19:41');

INSERT INTO `heart` (`postId`, `userId`) VALUES (5,3),(3,4),(7,4),(3,5),(6,5),(3,6),(4,6),(5,6),(7,6),(8,6),(6,7);

INSERT INTO `hashtag` (`hashtag`, `postId`) VALUES ('europe',4),('vacation',4),('lakers',5),('europe',6),('lakers',8),('warriors',8);

INSERT INTO `follow` (`followerUserId`, `followeeUserId`) VALUES (4,3),(5,3),(6,3),(7,3),(3,4),(6,4),(6,5),(3,6),(5,6),(7,6),(6,7);

INSERT INTO `comment` (`commentId`, `postId`, `userId`, `content`, `commentDate`) VALUES (1,3,5,'Nice! I got back from vacation this last week','2025-03-23 22:10:51'),(2,5,6,'Let\'s gooo!','2025-03-23 22:11:47'),(3,4,6,'No way, I\'ll be visiting there next month','2025-03-23 22:13:49'),(4,6,5,'You should def check out Tonnarello if you\'re stopping by Rome!','2025-03-23 22:14:45'),(5,5,3,'I was there!!','2025-03-23 22:15:41'),(6,7,4,'yessir!','2025-03-23 22:16:55'),(7,7,3,'No way Lebron just commented on my post ','2025-03-23 22:17:26'),(8,7,7,'boooo','2025-03-23 22:18:49'),(9,8,6,'nah you\'re delusional','2025-03-23 22:20:09');

INSERT INTO `bookmark` (`postId`, `userId`) VALUES (5,6),(7,6);