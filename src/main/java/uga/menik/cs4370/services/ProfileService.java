package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.Post;


@Service
public class ProfileService {
  
    private final UserService userService;
    private final DataSource dataSource;

    @Autowired
    public ProfileService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public List<Post> getProfilePosts(String userId) {
        System.out.println("Getting profile for user: " + userId);
        List<Post> posts = new ArrayList<>();
        final String sql = "SELECT postId, userId, content, postDate, heartsCount, commentsCount, isHearted, isBookmarked " +
                           "FROM post WHERE userId = ?";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Assuming the original date format is like "2025-03-07 10:54:00"
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a"); // Format: "Mar 07, 2025, 10:54 PM"
    
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
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
                    userService.getUserById(userId),
                    rs.getInt("heartsCount"),
                    rs.getInt("commentsCount"),
                    rs.getBoolean("isHearted"),
                    rs.getBoolean("isBookmarked")
                );
                posts.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }
    
}
