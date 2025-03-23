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
  
    private final PostService postService;

    @Autowired
    public ProfileService(PostService postService) {
        this.postService = postService;
    }

    public List<Post> getProfilePosts(String userId) {
        return postService.getPostsByUserId(userId);
    }
    
}
