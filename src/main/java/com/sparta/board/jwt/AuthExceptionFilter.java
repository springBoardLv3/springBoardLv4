package com.sparta.board.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.board.dto.StatusCodesResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "AuthFilter exception")
//@Component
@RequiredArgsConstructor
public class AuthExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        //다음 필터인 AuthFilter를 try하고 AuthFilter에서 던진 에러를 여기서 캐치한다.
        try{
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            // response body에 넣을 json 형태의 값
            StatusCodesResponseDto statusCodesResponseDto = new StatusCodesResponseDto(
                    HttpStatus.BAD_REQUEST.value(),
                    "토큰이 유효하지 않습니다.");

            String json = new ObjectMapper().writeValueAsString(statusCodesResponseDto);

            response.getWriter().write(json);
        }
    }
}