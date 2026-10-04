package com.sami9889.aiphonesupport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${app.security.enabled:false}") boolean securityEnabled,
            @Value("${app.security.webhook-token:}") String webhookToken) throws Exception {
        if (securityEnabled && !StringUtils.hasText(webhookToken)) {
            throw new IllegalStateException("WEBHOOK_SHARED_SECRET is required when production security is enabled.");
        }

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/prometheus").permitAll();
                    if (securityEnabled) {
                        authorize.requestMatchers("/api/calls/inbound", "/api/calls/handle-speech").permitAll()
                                .anyRequest().authenticated();
                    } else {
                        authorize.anyRequest().permitAll();
                    }
                });

        if (securityEnabled) {
            http.addFilterBefore(new WebhookTokenFilter(webhookToken), UsernamePasswordAuthenticationFilter.class)
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }

    private static class WebhookTokenFilter extends org.springframework.web.filter.OncePerRequestFilter {

        private final byte[] expectedToken;

        private WebhookTokenFilter(String expectedToken) {
            this.expectedToken = expectedToken.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        protected void doFilterInternal(
                jakarta.servlet.http.HttpServletRequest request,
                jakarta.servlet.http.HttpServletResponse response,
                jakarta.servlet.FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {
            String path = request.getServletPath();
            boolean isCallWebhook = "POST".equals(request.getMethod())
                    && ("/api/calls/inbound".equals(path) || "/api/calls/handle-speech".equals(path));
            if (isCallWebhook) {
                String providedToken = request.getHeader("X-Webhook-Token");
                if (providedToken == null || !java.security.MessageDigest.isEqual(
                        expectedToken, providedToken.getBytes(StandardCharsets.UTF_8))) {
                    response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}