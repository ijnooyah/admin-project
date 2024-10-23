package com.yoonji.adminproject.docs.post.controller;

import com.yoonji.adminproject.comment.dto.request.CommentCondition;
import com.yoonji.adminproject.comment.dto.request.CommentCreateRequest;
import com.yoonji.adminproject.comment.dto.request.CommentRequest;
import com.yoonji.adminproject.comment.dto.request.CommentUpdateRequest;
import com.yoonji.adminproject.comment.dto.response.CommentListResponse;
import com.yoonji.adminproject.comment.dto.response.CommentResponse;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.post.dto.request.PostRequest;
import com.yoonji.adminproject.post.dto.response.PostResponse;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Post", description = "Post API")
public interface PostControllerDocs {

    @Operation(summary = "게시물 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
    })
    CommonResponse<PostResponse> getPost(@PathVariable Long postId);

    @Operation(summary = "게시물 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 작성 성공"),
    })
    CommonResponse<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserPrincipal principal);


    @Operation(summary = "게시물 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 수정 성공"),
    })
    CommonResponse<PostResponse> updatePost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId);

    @Operation(summary = "게시물 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
    })
    CommonResponse<Void> deletePost(@PathVariable Long postId,
                                    @AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "게시물의 댓글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물의 댓글 조회 성공"),
    })
    CommonResponse<CommentListResponse> getComments(@PathVariable Long postId, @ParameterObject CommentCondition condition);

    @Operation(summary = "댓글 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
    })
    CommonResponse<CommentResponse> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId);


    @Operation(summary = "댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
    })
    CommonResponse<CommentResponse> updateComment(
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId);

    @Operation(summary = "댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
    })
    CommonResponse<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal);

}
