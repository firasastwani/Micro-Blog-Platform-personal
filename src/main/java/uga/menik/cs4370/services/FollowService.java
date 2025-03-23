package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class to handle follow-related operations.
 */
@Service
public class FollowService {
    
    private final DataSource dataSource;

    @Autowired
    public FollowService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Toggles the follow state. If currently following, it will unfollow.
     * If not currently following, it will follow.
     *
     * @param followerUserId ID of the user who is following
     * @param followeeUserId ID of the user being followed
     * @param isFollow ignored as we check the current state
     * @return true if operation was successful, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean followUnfollowUser(String followerUserId, String followeeUserId, boolean isFollow) 
            throws SQLException {
        if (followerUserId.equals(followeeUserId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        // Check current follow state
        boolean currentlyFollowing = isFollowing(followerUserId, followeeUserId);
        
        // Toggle: if following, unfollow; if not following, follow
        String sql = currentlyFollowing 
            ? "DELETE FROM follow WHERE followerUserId = ? AND followeeUserId = ?"
            : "INSERT INTO follow (followerUserId, followeeUserId) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(followerUserId));
            stmt.setInt(2, Integer.parseInt(followeeUserId));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Checks if one user is following another.
     *
     * @param followerUserId ID of the potential follower
     * @param followeeUserId ID of the potential followee
     * @return true if followerUserId is following followeeUserId
     * @throws SQLException if database operation fails
     */
    public boolean isFollowing(String followerUserId, String followeeUserId) throws SQLException {
        String sql = "SELECT 1 FROM follow WHERE followerUserId = ? AND followeeUserId = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(followerUserId));
            stmt.setInt(2, Integer.parseInt(followeeUserId));
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
