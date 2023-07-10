package com.sparta.board.controller;

import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponseDto;
import com.sparta.board.dto.StatusCodesResponseDto;
import com.sparta.board.entity.User;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.security.UserDetailsImpl;
import com.sparta.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 특정 게시글에 댓글 작성
    @PostMapping("/{boardId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long boardId,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.createComment(boardId, requestDto, user), HttpStatus.CREATED) ;
    }

    // 특정 게시글의 댓글 수정
    @PutMapping("/{commentid}")
    public ResponseEntity<CommentResponseDto> update(@PathVariable Long commentid,
                                                     @RequestBody @Valid CommentRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.updateComment(commentid, requestDto, user), HttpStatus.OK);
    }

    // 특정 게시글의 댓글 삭제
    @DeleteMapping("/{commentid}")
    public ResponseEntity<StatusCodesResponseDto> deleteBoard(@PathVariable Long commentid,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.deleteComment(commentid, user), HttpStatus.OK);
    }
}
