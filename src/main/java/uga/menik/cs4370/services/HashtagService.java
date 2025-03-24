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
        List<Post> posts = new ArrayList<>();
        
        // Clean the hashtags (remove '#' if present)
        List<String> cleanedHashtags = new ArrayList<>();
        for (String hashtag : hashtagArray) {
            cleanedHashtags.add(hashtag.replace("#", ""));
        }
        
        if (cleanedHashtags.isEmpty()) {
            return posts;
        }

        // Build SQL query to find posts that contain all hashtags
        final String sql = """
            SELECT postId 
            FROM hashtag 
            WHERE hashtag IN (%s)
            GROUP BY postId 
            HAVING COUNT(DISTINCT hashtag) = ?
            """.formatted(String.join(",", "?".repeat(cleanedHashtags.size()).split("")));
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set all hashtag parameters
            for (int i = 0; i < cleanedHashtags.size(); i++) {
                stmt.setString(i + 1, cleanedHashtags.get(i));
            }
            // Set the count parameter (number of hashtags that must match)
            stmt.setInt(cleanedHashtags.size() + 1, cleanedHashtags.size());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int postId = rs.getInt("postId");
                    posts.addAll(postService.getPosts("WHERE p.postId = ? ORDER BY p.postDate DESC", postId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return posts;
    }

}
