package com.example.backend.configs;

import com.example.backend.securities.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ========== PUBLIC ENDPOINTS ==========
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()

                        // ========== ADMIN ONLY ENDPOINTS ==========
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/tutors").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Grade Management (Admin only)
                        .requestMatchers("/api/grades/**").hasRole("ADMIN")
                        .requestMatchers("/api/grade-progression/**").hasRole("ADMIN")

                        // Notifications (Admin only)
                        .requestMatchers("/api/notifications/**").hasRole("ADMIN")

                        // ========== ADMIN + TUTOR ENDPOINTS ==========
                        // Student Management
                        .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TUTOR")

                        // Tutor Management
                        .requestMatchers("/api/tutors/**").hasAnyRole("ADMIN", "TUTOR")

                        // Subject & Subtype Management
                        .requestMatchers("/api/subjects/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/hourly-group-subtypes/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/monthly-subtypes/**").hasAnyRole("ADMIN", "TUTOR")

                        // Class Management (3 types based on DB v3)
                        .requestMatchers("/api/hourly-group-classes/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/hourly-individual-classes/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/monthly-classes/**").hasAnyRole("ADMIN", "TUTOR")

                        // Enrollment Management
                        .requestMatchers("/api/hourly-group-enrollments/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/monthly-enrollments/**").hasAnyRole("ADMIN", "TUTOR")

                        // Hour Forms (Teaching Records)
                        .requestMatchers("/api/hour-forms/**").hasAnyRole("ADMIN", "TUTOR")

                        // Receipt Management
                        .requestMatchers("/api/receipts/**").hasAnyRole("ADMIN", "TUTOR")

                        // Reports & Statistics
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/statistics/**").hasAnyRole("ADMIN", "TUTOR")

                        // ========== DEFAULT ==========
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins in development (ควรกำหนด specific origins ใน production)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Expose headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Max age for preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}