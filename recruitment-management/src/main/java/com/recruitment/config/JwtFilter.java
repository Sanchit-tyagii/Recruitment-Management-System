package com.recruitment.config;

import com.recruitment.model.User;
import com.recruitment.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.getSubject(token);
                    Optional<User> userOpt = userRepository.findByEmail(email);

                    if (userOpt.isPresent()) {
                        User user = userOpt.get();

                        // Add role (and ensure it has ROLE_ prefix)
                        String role = "ROLE_" + user.getUserType().name();
                        var authorities = List.of(new SimpleGrantedAuthority(role));

                        // Create authentication object
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication context
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Optional: attach metadata to request
                        request.setAttribute("currentUserId", user.getId());
                        request.setAttribute("currentUserEmail", user.getEmail());
                        request.setAttribute("currentUserType", user.getUserType().name());
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired token");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error in JwtFilter");
        } finally {
            // Clear context after each request for safety in stateless mode
            SecurityContextHolder.clearContext();
        }
    }
}
