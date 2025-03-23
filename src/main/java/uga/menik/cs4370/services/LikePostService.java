package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikePostService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public LikePostService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public boolean addHeart(String postId) throws SQLException {
        String userId = userService.getLoggedInUser().getUserId();
        String insertQuery = "INSERT INTO heart (userId, postId) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Added heart, rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    public boolean removeHeart(String postId) throws SQLException {
        String userId = userService.getLoggedInUser().getUserId();
        String query = "DELETE FROM heart WHERE userId = ? AND postId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Removed heart, rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    public boolean isHearted(String postId) throws SQLException {
        String userId = userService.getLoggedInUser().getUserId();
        String query = "SELECT COUNT(*) as count FROM heart WHERE userId = ? AND postId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean result = rs.getInt("count") > 0;
                    System.out.println("Checking heart status - userId: " + userId + 
                                     ", postId: " + postId + ", result: " + result);
                    return result;
                }
            }
        }
        return false;
    }

    public int getHeartCount(String postId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM heart WHERE postId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(postId));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
}
