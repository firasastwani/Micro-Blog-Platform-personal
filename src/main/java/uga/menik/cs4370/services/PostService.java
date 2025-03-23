package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.models.User;


@Service
public class PostService {
    
    private final UserService userService;
    private final DataSource dataSource;

    @Autowired
    public PostService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }
    
    // Base method to get posts with all necessary information
    public List<Post> getPosts(String whereClause, Object... params) {
        List<Post> posts = new ArrayList<>();
        String sql = """
            SELECT 
                p.*,
                (SELECT COUNT(*) FROM heart h WHERE h.postId = p.postId) as heartsCount,
                (SELECT COUNT(*) FROM comment c WHERE c.postId = p.postId) as commentsCount,
                (SELECT COUNT(*) > 0 FROM heart h WHERE h.postId = p.postId AND h.userId = ?) as isHearted,
                (SELECT COUNT(*) > 0 FROM bookmark b WHERE b.postId = p.postId AND b.userId = ?) as isBookmarked
            FROM post p
            """ + (whereClause != null ? whereClause : "");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String currentUserId = userService.getLoggedInUser().getUserId();
            // Set the standard parameters for bookmark and heart checks
            stmt.setInt(1, Integer.parseInt(currentUserId));
            stmt.setInt(2, Integer.parseInt(currentUserId));
            
            // Set any additional parameters from the whereClause
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 3, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String postDateString = rs.getString("postDate");
                Date postDate = null;
                if (postDateString != null) {
                    postDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(postDateString);
                }
                String formattedPostDate = (postDate != null) ? 
                    new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(postDate) : "";

                Post post = new Post(
                    rs.getString("postId"),
                    rs.getString("content"),
                    formattedPostDate,
                    userService.getUserById(Integer.toString(rs.getInt("userId"))),
                    rs.getInt("heartsCount"),
                    rs.getInt("commentsCount"),
                    rs.getInt("isHearted") > 0,
                    rs.getInt("isBookmarked") > 0
                );
                posts.add(post);
            }
        } catch (Exception e) {
            System.err.println("Error getting posts: " + e.getMessage());
            e.printStackTrace();
        }
        Collections.reverse(posts);
        return posts;
    }

    // Method for getting all posts (home page)
    public List<Post> getPosts() {
        String whereClause = """
            WHERE p.userId IN (
                SELECT followeeUserId 
                FROM follow 
                WHERE followerUserId = ?
            )
            ORDER BY p.postDate DESC
            """;
        return getPosts(whereClause, 
            Integer.parseInt(userService.getLoggedInUser().getUserId()));
    }

    // Method for getting posts by user ID (profile page)
    public List<Post> getPostsByUserId(String userId) {
        return getPosts("WHERE p.userId = ?", Integer.parseInt(userId));
    }

    // Method for getting bookmarked posts
    public List<Post> getBookmarkedPosts(String userId) {
        return getPosts(
            "INNER JOIN bookmark b ON p.postId = b.postId WHERE b.userId = ?", 
            Integer.parseInt(userId)
        );
    }

    // Method for getting a single post by ID
    public Post getPostById(int postId) {
        List<Post> posts = getPosts("WHERE p.postId = ?", postId);
        return posts.isEmpty() ? null : posts.get(0);
    }
    

    public boolean addPost(String content) {

        final String insertPostSQL = "INSERT INTO post (userId, content) VALUES (?, ?)";
        final String insertHashtagSQL = "INSERT INTO hashtag (hashtag, postId) VALUES (?, ?)";
        final String getLastPostIdSQL = "SELECT MAX(postId) FROM post";

        // Get logged-in user
        User user = userService.getLoggedInUser();
        if (user == null) {
            System.out.println("User is not logged in.");
            return false;
        }

        // Convert userId (String) to int
        int userId;
        try {
            userId = Integer.parseInt(user.getUserId());
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID format: " + user.getUserId());
            return false;
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert post
            int postId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertPostSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, userId);
                stmt.setString(2, content);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                                    postId = generatedKeys.getInt(1);
                        }
                    }
                }
            }

            // If postId was not retrieved, use MAX(postId)
            if (postId == -1) {
                    try (PreparedStatement stmt = conn.prepareStatement(getLastPostIdSQL);
                    ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        postId = rs.getInt(1);
                    }
                }
            }

            // Extract hashtags from content
            Pattern pattern = Pattern.compile("#(\\w+)");
            Matcher matcher = pattern.matcher(content);

            // Insert hashtags if found
            if (postId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(insertHashtagSQL)) {
                    while (matcher.find()) {
                        stmt.setString(1, matcher.group(1));
                        stmt.setInt(2, postId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
