package com.sparta.board.config;

import com.sparta.board.jwt.AuthExceptionFilter;
import com.sparta.board.jwt.JwtAuthorizationFilter;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

//    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public AuthExceptionFilter authExceptionFilter() {
//        return new AuthExceptionFilter();
//    }
//
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, userDetailsService);
//        return jwtAuthorizationFilter;
//    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers(new AntPathRequestMatcher("/users/**"))
                    .requestMatchers( new AntPathRequestMatcher("/boards", "GET"));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers("/users/**").permitAll() // '/users/'로 시작하는 요청 모두 접근 허가  . 권한이 있지 않지만 통과시켜준다.
                        .requestMatchers(HttpMethod.GET, "/boards/**").permitAll() // '/boards/'로 시작하는 요청중 get method 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
                //permitAll과 ignoring의 차이점...
        );

//        http.formLogin((formLogin) -> {
//            System.out.println("login test");
//            formLogin
//                    .loginPage("/api/user/login-page").permitAll();
//        });


        // 필터 관리  +@Order로 순서를 정하면 정상적으로 작동하지 않음. 여러 필터들의 순서가 꼬이기 때문이지 않을까?
        http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil,userDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new AuthExceptionFilter(), JwtAuthorizationFilter.class);
        return http.build();
    }
}