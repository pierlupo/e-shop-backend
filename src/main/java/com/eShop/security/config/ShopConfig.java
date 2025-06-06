package com.eShop.security.config;

import com.eShop.security.jwt.AuthTokenFilter;
import com.eShop.security.jwt.JwtAuthEntryPoint;
import com.eShop.security.jwt.JwtUtils;
import com.eShop.security.user.ShopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class ShopConfig {

    private static final List<String> SECURED_URLS =
            List.of("/api/v1/carts/**","/api/v1/cartItems/**");
    private final ShopUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtUtils jwtUtils;


    @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthTokenFilter authTokenFilter()  {
            return new AuthTokenFilter(jwtUtils, userDetailsService);
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }

        @Bean
        public MultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            var authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .formLogin(AbstractHttpConfigurer::disable)
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)
                    .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/uploads/**").permitAll()
                            .requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                            .anyRequest()
                            .permitAll());
            http.authenticationProvider(authenticationProvider());
            http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
}