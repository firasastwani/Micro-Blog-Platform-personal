package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;



@Service
public class BookmarkService {

    private final DataSource dataSource;

    @Autowired 
    public BookmarkService(DataSource dataSource){
        this.dataSource = dataSource; 
    }

    public boolean addBookmark(String userId, String postId) throws SQLException {
        if (isBookmarked(userId, postId)) {
            System.out.println("Removing existing bookmark for userId=" + userId + ", postId=" + postId);
            return removeBookmark(userId, postId);
        }
        
        String query = "INSERT INTO bookmark (userId, postId) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Added bookmark, rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    public boolean removeBookmark(String userId, String postId) throws SQLException {
        String query = "DELETE FROM bookmark WHERE userId = ? AND postId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Removed bookmark, rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }
    

    public boolean isBookmarked(String userId, String postId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM bookmark WHERE userId = ? AND postId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(postId));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean result = rs.getInt("count") > 0;
                    System.out.println("Checking bookmark status - userId: " + userId + 
                                     ", postId: " + postId + ", result: " + result);
                    return result;
                }
            }
        }
        return false;
    }

}



