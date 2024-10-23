package com.yoonji.adminproject.post.controller;


import com.yoonji.adminproject.comment.dto.request.CommentCondition;
import com.yoonji.adminproject.comment.dto.request.CommentCreateRequest;
import com.yoonji.adminproject.comment.dto.request.CommentUpdateRequest;
import com.yoonji.adminproject.comment.dto.response.CommentListResponse;
import com.yoonji.adminproject.comment.dto.response.CommentResponse;
import com.yoonji.adminproject.comment.service.CommentService;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.post.controller.PostControllerDocs;
import com.yoonji.adminproject.post.dto.request.PostRequest;
import com.yoonji.adminproject.post.dto.response.PostResponse;
import com.yoonji.adminproject.post.service.PostService;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public CommonResponse<PostResponse> getPost(@PathVariable Long postId) {
        return new CommonResponse<>(HttpStatus.OK, postService.getPost(postId));
    }

    @PostMapping
    public CommonResponse<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new CommonResponse<>(HttpStatus.OK, postService.createPost(request, principal));
    }

    @PatchMapping("/{postId}")
    public CommonResponse<PostResponse> updatePost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId) {
        return new CommonResponse<>(HttpStatus.OK, postService.updatePost(request, principal, postId));
    }

    @DeleteMapping("/{postId}")
    public CommonResponse<Void> deletePost(@PathVariable Long postId,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        postService.deletePost(postId, principal);
        return new CommonResponse<>(HttpStatus.OK);
    }

    // == 댓글 ==

    @GetMapping("/{postId}/comments")
    public CommonResponse<CommentListResponse> getComments(@PathVariable Long postId, @ParameterObject CommentCondition condition) {
        return new CommonResponse<>(HttpStatus.OK, commentService.getComments(condition, postId));
    }

    @PostMapping("/{postId}/comments")
    public CommonResponse<CommentResponse> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId) {
        return new CommonResponse<>(HttpStatus.OK, commentService.createComment(request, principal, postId));
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public CommonResponse<CommentResponse> updateComment(
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        return new CommonResponse<>(HttpStatus.OK, commentService.updateComment(request, principal, postId, commentId));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public CommonResponse<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(postId, commentId, principal);
        return new CommonResponse<>(HttpStatus.OK);
    }
}
