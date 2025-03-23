package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.Post;

@Service
public class HashtagService {

    private final DataSource dataSource;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public HashtagService(DataSource dataSource, UserService userService, PostService postService) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.postService = postService;
    }

    public List<Post> searchHashtag(String hashtags) {
        // Remove the "#" from each hashtag and split the input string by spaces
        String[] hashtagArray = hashtags.split("\\s+");
        
        List<Integer> postIds = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        
        // Iterate over each hashtag and fetch associated posts
        for (String hashtag : hashtagArray) {
            // Clean the hashtag (remove '#' if present)
            hashtag = hashtag.replace("#", "");
    
            final String sql = "SELECT postId FROM hashtag WHERE hashtag = ?";
            
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
        }
        
        // Fetch post details for each postId
        for (int postId : postIds) {
            posts.addAll(postService.getPosts("WHERE p.postId = ?", postId));
        }
        
        return posts;
    }

}
