--This handles bookmarking the user post
INSERT INTO bookmark (userId, postId) VALUES (?, ?)
http://localhost:8081/bookmarks

--This handles removing bookmark from user post
DELETE FROM bookmark WHERE userId = ? AND postId = ?
http://localhost:8081/bookmarks

--This handles adding comment in expanded post
INSERT INTO comment (postId, userId, content) VALUES (?, ?, ?)
http://localhost:8081/post/8

--This handles getting all the comments with the selected post
SELECT commentId, content, commentDate, userId FROM comment WHERE postId = ?
http://localhost:8081/post/8

--This handles unfollow the user if the user was already followed
DELETE FROM follow WHERE followerUserId = ? AND followeeUserId = ?
http://localhost:8081/people

--This handles follow the user if the user was not followed
INSERT INTO follow (followerUserId, followeeUserId) VALUES (?, ?)
http://localhost:8081/people

--This handles getting status of user, whatever they are followed or not followed
SELECT 1 FROM follow WHERE followerUserId = ? AND followeeUserId = ?
http://localhost:8081/people

--This handles getting postid with certain hashtag
SELECT postId FROM hashtag WHERE hashtag = ?
http://localhost:8081/hashtagsearch?hashtags=lakers

--This handles Liking the post
INSERT INTO heart (userId, postId) VALUES (?, ?)
http://localhost:8081/post/8

--This handles unliking the post
DELETE FROM heart WHERE userId = ? AND postId = ?
http://localhost:8081/post/8

--This handles getting the status of likes of current user
--Whatever they liked current post or not
SELECT COUNT(*) as count FROM heart WHERE userId = ? AND postId = ?
http://localhost:8081/post/8

--This handles getting the number of likes of current post
SELECT COUNT(*) as count FROM heart WHERE postId = ?
http://localhost:8081/post/8

--This handles getting all the followble user and their last post date
SELECT 
                u.*,
                COALESCE(
                    DATE_FORMAT(
                        (SELECT MAX(postDate) 
                         FROM post p 
                         WHERE p.userId = u.userId),
                        '%b %d, %Y, %h:%i %p'
                    ),
                    'Never posted'
                ) as lastActiveDate,
                EXISTS (
                    SELECT 1 
                    FROM follow f 
                    WHERE f.followerUserId = ? 
                    AND f.followeeUserId = u.userId
                ) as isFollowed
            FROM user u
            WHERE u.userId != ?
http://localhost:8081/people


--This handles getting post information while using joins with other tables (#post)
SELECT 
                p.*,
                (SELECT COUNT(*) FROM heart h WHERE h.postId = p.postId) as heartsCount,
                (SELECT COUNT(*) FROM comment c WHERE c.postId = p.postId) as commentsCount,
                (SELECT COUNT(*) > 0 FROM heart h WHERE h.postId = p.postId AND h.userId = ?) as isHearted,
                (SELECT COUNT(*) > 0 FROM bookmark b WHERE b.postId = p.postId AND b.userId = ?) as isBookmarked
            FROM post p
            """
http://localhost:8081/


--This where clause handles getting all the posts in home page (get all posts of followed user)
--This where clause is intended to work together with select clause marked with #post
WHERE p.userId IN (
                SELECT followeeUserId 
                FROM follow 
                WHERE followerUserId = ?
            )
            ORDER BY p.postDate DESC
http://localhost:8081/

--This handles getting bookmarked post
--This  is intended to work together with select clause marked with #post
INNER JOIN bookmark b ON p.postId = b.postId WHERE b.userId = ?
http://localhost:8081/bookmarks

--This handles adding post 
INSERT INTO post (userId, content) VALUES (?, ?)
INSERT INTO hashtag (hashtag, postId) VALUES (?, ?)
SELECT MAX(postId) FROM post
http://localhost:8081/



