package org.example.javaspringboot_security_online_course_jlkesh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javaspringboot_security_online_course_jlkesh.dto.AppErrorDto;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final View error;

    public SecurityConfig(View error) {
        this.error = error;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MessageSource messageSource) throws Exception {
            http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                    .exceptionHandling(customizer -> customizer
                            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                            .accessDeniedHandler(new CustomAccessDeniedHandler() )
                    );
        return http.build();
    }

    static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            authException.printStackTrace();
            int errorCode = HttpServletResponse.SC_UNAUTHORIZED;
            AppErrorDto appErrorDto = new AppErrorDto(
                    authException.getMessage(),
                    request.getRequestURI(),
                    errorCode,
                    LocalDateTime.now()
            );

            response.setStatus(errorCode);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(), appErrorDto);
        }
    }

    static class CustomAccessDeniedHandler implements AccessDeniedHandler{

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            int errorCode = HttpServletResponse.SC_FORBIDDEN;
            response.setStatus(errorCode);
            response.setContentType("application/json");

            String jsonResponse = String.format(
                    "{\"errorMessage\": \"%s\", \"errorPath\": \"%s\", \"errorCode\": %d, \"timestamp\": \"%s\"}",
                    accessDeniedException.getMessage(),
                    request.getRequestURI(),
                    errorCode,
                    LocalDateTime.now()
            );

            response.getWriter().write(jsonResponse);
        }
    }

    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails admin = User.builder()
                .username("admin")
                .password("123")
                .roles("ADMIN", "MANAGER")
                .build();
        UserDetails manager = User.builder()
                .username("manager")
                .password("123")
                .roles("MANAGER")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password("123")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, manager, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:8080",
                "http://localhost:9090"
        ));
        configuration.setAllowedHeaders(List.of( "*"
//                "Accept",
//                "Content-Type",
//                "Authorization"
        ));
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE"
        ));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
