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
    
    public List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        final String sql = "SELECT postId, userId, content, postDate, heartsCount, commentsCount, isHearted, isBookmarked " +
                           "FROM post";
    
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Assuming the original date format is like "2025-03-07 10:54:00"
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a"); // Format: "Mar 07, 2025, 10:54 PM"
    
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                String postDateString = rs.getString("postDate");
                Date postDate = null;
    
                // Parse the postDate string into a Date object
                if (postDateString != null) {
                    postDate = inputFormat.parse(postDateString);
                }

                // Format the date to the desired format
                String formattedPostDate = (postDate != null) ? outputFormat.format(postDate) : "";
                Post post = new Post(
                    rs.getString("postId"),
                    rs.getString("content"),
                    formattedPostDate,  
                    userService.getUserById(Integer.toString(rs.getInt("userId"))),
                    rs.getInt("heartsCount"),
                    rs.getInt("commentsCount"),
                    rs.getBoolean("isHearted"),
                    rs.getBoolean("isBookmarked")
                );
    
                posts.add(post);
            }
    
        } catch (Exception e) {
            e.printStackTrace(); // Print error for debugging
        }
    
        return posts;
    }
    
    public Post getPostById(int postId) {
        final String sql = "SELECT postId, userId, content, postDate, heartsCount, commentsCount, isHearted, isBookmarked " +
                           "FROM post WHERE postId = ?";
    
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Original format
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a"); // Desired format
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String postDateString = rs.getString("postDate");
                Date postDate = null;
    
                if (postDateString != null) {
                    postDate = inputFormat.parse(postDateString);
                }
    
                // Format the date to the desired format
                String formattedPostDate = (postDate != null) ? outputFormat.format(postDate) : "";
    
                return new Post(
                    rs.getString("postId"),  // Use correct type
                    rs.getString("content"),
                    formattedPostDate,
                    userService.getUserById(Integer.toString(rs.getInt("userId"))),
                    rs.getInt("heartsCount"),
                    rs.getInt("commentsCount"),
                    rs.getBoolean("isHearted"),
                    rs.getBoolean("isBookmarked")
                );
            }
    
        } catch (Exception e) {
            e.printStackTrace(); // Print error for debugging
        }
    
        return null; // Return null if the post is not found
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
