package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        final String sql = "INSERT INTO post (userId, content) VALUES (?, ?)";
    
        // Get logged-in user
        User user = userService.getLoggedInUser();

        // Convert userId (String) to int
        int userId;
        
        try {
            userId = Integer.parseInt(user.getUserId());
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID format.");
            return false;
        }
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, userId);  // Set userId
            stmt.setString(2, content);  // Set post content
    
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    

}
