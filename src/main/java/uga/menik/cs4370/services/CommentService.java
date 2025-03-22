package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.Comment;
import uga.menik.cs4370.models.ExpandedPost;
import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.models.User;

@Service
public class CommentService {
    private final UserService userService;
    private final DataSource dataSource;
    private final PostService postService;

    @Autowired
    public CommentService(DataSource dataSource, UserService userService, PostService postService) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.postService = postService;
    }

    public boolean addComment(String postId, String comment){

        int postID = Integer.parseInt(postId);
        User user = userService.getLoggedInUser();
        // Convert userId (String) to int
        int userId;
        userId = Integer.parseInt(user.getUserId());

        final String sql = "INSERT INTO comment (postId, userId, content) VALUES (?,?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, postID);  // Set userId
            stmt.setInt(2, userId);  // Set post content
            stmt.setString(3,comment);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ExpandedPost> getExpandedPostWithComments(int postId) {

        List<Comment> commentsForPost = new ArrayList<>();
        final String sql = "SELECT commentId, content, commentDate, userId FROM comment WHERE postId = ?";
    
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Assuming the original date format is like "2025-03-07 10:54:00"
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a"); // Format: "Mar 07, 2025, 10:54 PM"
    
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String commentDateString = rs.getString("commentDate");
                Date commentDate = null;
    
                // Parse the postDate string into a Date object
                if (commentDateString != null) {
                    commentDate = inputFormat.parse(commentDateString);
                }
    
                // Format the date to the desired format
                String formattedPostDate = (commentDate != null) ? outputFormat.format(commentDate) : "";
    
                Comment comment = new Comment(
                    rs.getString("commentId"),
                    rs.getString("content"),
                    formattedPostDate,  
                    userService.getUserById(Integer.toString(rs.getInt("userId")))      
                );
                commentsForPost.add(comment);
            }

            Post post = postService.getPostById(postId);

            ExpandedPost postWithComments = new ExpandedPost(post.getPostId(), post.getContent(), post.getPostDate(), 
            post.getUser(), post.getHeartsCount(), commentsForPost.size(), post.getHearted(), post.isBookmarked(), commentsForPost);

            return List.of(postWithComments);

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
