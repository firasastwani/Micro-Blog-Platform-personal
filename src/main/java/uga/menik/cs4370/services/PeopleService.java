/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.FollowableUser;


/**
 * This service contains people related functions.
 */
@Service
public class PeopleService {

    // dataSource enables talking to the database.
    private final DataSource dataSource;
    // passwordEncoder is used for password security.

    /**
     * See AuthInterceptor notes regarding dependency injection and
     * inversion of control.
     */
    @Autowired
    public PeopleService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This function should query and return all users that 
     * are followable. The list should not contain the user 
     * with id userIdToExclude.
     */
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) {
        List<FollowableUser> followableUsers = new ArrayList<>();
        String sql = """
            SELECT 
                u.*,
                COALESCE(
                    DATE_FORMAT(
                        (SELECT MAX(postDate) 
                         FROM post p 
                         WHERE p.userId = u.userId),
                        '%b %d, %Y, %h:%i %p'
                    ),
                    'Never posted'
                ) as lastActiveDate,
                EXISTS (
                    SELECT 1 
                    FROM follow f 
                    WHERE f.followerUserId = ? 
                    AND f.followeeUserId = u.userId
                ) as isFollowed
            FROM user u
            WHERE u.userId != ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userIdToExclude);  // For the EXISTS subquery
            stmt.setString(2, userIdToExclude);  // For the WHERE clause
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FollowableUser user = new FollowableUser(
                    rs.getString("userId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getBoolean("isFollowed"),  // Changed from false to actual follow status
                    rs.getString("lastActiveDate")
                );
                followableUsers.add(user);
            }

        } catch (Exception e) {
            System.out.println("ERROR");
        }

        return followableUsers;
    }
    

}
