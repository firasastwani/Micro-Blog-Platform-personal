/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.cs4370.models.ExpandedPost;
import uga.menik.cs4370.services.CommentService;
import uga.menik.cs4370.services.PostService;
import uga.menik.cs4370.services.UserService;
import uga.menik.cs4370.services.BookmarkService;
import uga.menik.cs4370.services.LikePostService;
import org.springframework.beans.factory.annotation.Autowired;
import uga.menik.cs4370.models.User;



/**
 * Handles /post URL and its sub urls.
 */
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService; 
    private final BookmarkService bookmarkService; 
    private final LikePostService likePostService; 

    
    public PostController(PostService postService, CommentService commentService, UserService userService, BookmarkService bookmarkService, LikePostService likePostService){
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService; 
        this.bookmarkService = bookmarkService; 
        this.likePostService = likePostService; 
    }

    /**
     * This function handles the /post/{postId} URL.
     * This handlers serves the web page for a specific post.
     * Note there is a path variable {postId}.
     * An example URL handled by this function looks like below:
     * http://localhost:8081/post/1
     * The above URL assigns 1 to postId.
     * 
     * See notes from HomeController.java regardig error URL parameter.
     */
    @GetMapping("/{postId}")
    public ModelAndView showExpandedPost(@PathVariable("postId") String postId) {
        ModelAndView mv = new ModelAndView("posts_page");
        
        try {
            // Get the expanded post with comments using CommentService
            List<ExpandedPost> expandedPosts = commentService.getExpandedPostWithComments(Integer.parseInt(postId));
            
            if (!expandedPosts.isEmpty()) {
                mv.addObject("posts", expandedPosts);
            } else {
                mv.addObject("isNoContent", true);
                mv.addObject("errorMessage", "Post not found");
            }
        } catch (Exception e) {
            System.err.println("Error getting expanded post: " + e.getMessage());
            mv.addObject("errorMessage", "Failed to load post. Please try again.");
        }
        
        return mv;
    }

    /**
     * Handles comments added on posts.
     * See comments on webpage function to see how path variables work here.
     * This function handles form posts.
     * See comments in HomeController.java regarding form submissions.
     */
    @PostMapping("/{postId}/comment")
    public String postComment(@PathVariable("postId") String postId,
            @RequestParam(name = "comment") String comment) {
        System.out.println("The user is attempting add a comment:");
        System.out.println("\tpostId: " + postId);
        System.out.println("\tcomment: " + comment);

        // Redirect the user if the comment adding is a success.
        // return "redirect:/post/" + postId;
        try{
            boolean isAdded = commentService.addComment(postId, comment); //Add comment

            if (isAdded){//if add success
                return "redirect:/post/" + postId; 
            }else{ //if add fail
                String message = URLEncoder.encode("Failed to post the comment. Please try again.",
                StandardCharsets.UTF_8);
                return "redirect:/post/" + postId + "?error=" + message;

            }
            
        } catch (Exception e){
            String message = URLEncoder.encode("Failed to post the comment. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/post/" + postId + "?error=" + message;
        }
        // Redirect the user with an error message if there was an error.
        
    }

    /**
     * Handles likes added on posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions and how path variables work.
     */
    @GetMapping("/{postId}/heart/{isAdd}")
    public String addOrRemoveHeart(@PathVariable("postId") String postId,
            @PathVariable("isAdd") Boolean isAdd) {
        System.out.println("The user is attempting add or remove a heart:");
        System.out.println("\tpostId: " + postId);
        System.out.println("\tisAdd: " + isAdd);

        try {
            if (isAdd) {
                likePostService.addHeart(postId);
            } else {
                likePostService.removeHeart(postId);
            }
            return "redirect:/post/" + postId;
        } catch (SQLException e) {
            e.printStackTrace();
            String message = URLEncoder.encode("Failed to update heart status. Please try again.",
                    StandardCharsets.UTF_8);
            return "redirect:/post/" + postId + "?error=" + message;
        }
    }

    /**
     * Handles bookmarking posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions.
     */
    @GetMapping("/{postId}/bookmark/{isAdd}")
    public String addOrRemoveBookmark(
            @PathVariable("postId") String postId,
            @PathVariable("isAdd") Boolean isAdd) {
        try {
            User user = userService.getLoggedInUser();
            if (user != null) {
                bookmarkService.addBookmark(user.getUserId(), postId);
                // Redirect back to expanded post view
                return "redirect:/post/" + postId;
            }
        } catch (SQLException e) {
            System.err.println("Error handling bookmark: " + e.getMessage());
        }
        
        String message = URLEncoder.encode("Failed to update bookmark. Please try again.", 
                StandardCharsets.UTF_8);
        return "redirect:/post/" + postId + "?error=" + message;
    }

}
