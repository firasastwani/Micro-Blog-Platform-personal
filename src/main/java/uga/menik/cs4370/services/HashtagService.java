package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.Post;

@Service
public class HashtagService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public HashtagService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public List<Post> searchHashtag(String hashtag){
        final String sql = "SELECT postId FROM hashtag WHERE hashtag = ?";
        final String findPost = "SELECT postId, userId, content, postDate, heartsCount, commentsCount, isHearted, isBookmarked " +
                                "FROM post WHERE postId = ?";
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
                            
        List<Post> posts = new ArrayList<>();
        List<Integer> postIds = new ArrayList<>();
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, hashtag);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    postIds.add(rs.getInt("postId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Fetch post details for each postId
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findPost)) {
    
            for (int postId : postIds) {
                stmt.setInt(1, postId);
                
                try (ResultSet rs = stmt.executeQuery()) {


                    if (rs.next()) {
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
                } catch (ParseException e) {
                e.printStackTrace();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return posts;
    }
    
}
