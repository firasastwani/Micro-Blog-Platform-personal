package uga.menik.cs4370.controllers;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uga.menik.cs4370.services.LikePostService;

@Controller
@RequestMapping("/heart")
public class LikePostController {

    private final LikePostService likePostService;

    @Autowired
    public LikePostController(LikePostService likePostService) {
        this.likePostService = likePostService;
    }

    @PostMapping("/toggle/{postId}")
    public ResponseEntity<String> toggleHeart(@PathVariable String postId) {
        try {
            boolean isCurrentlyHearted = likePostService.isHearted(postId);
            boolean success;
            
            if (isCurrentlyHearted) {
                success = likePostService.removeHeart(postId);
            } else {
                success = likePostService.addHeart(postId);
            }
            
            if (success) {
                int newHeartCount = likePostService.getHeartCount(postId);
                return ResponseEntity.ok(String.valueOf(newHeartCount));
            } else {
                return ResponseEntity.badRequest().body("Failed to toggle heart");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Database error occurred");
        }
    }
} 