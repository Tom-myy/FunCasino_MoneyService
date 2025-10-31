package com.evofun.money.shared.security.config;

import com.evofun.money.shared.security.jwt.JwtUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationEntryPoint jwtAuthEntryPoint;
    private final AccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(AuthenticationEntryPoint jwtAuthEntryPoint,
                          AccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Value("${security.internal.issuer}")
    private String internalIssuer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/money/reservation/**").hasAuthority("SERVICE_GAME")
                        .requestMatchers("/api/v1/money/transfer/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/actuator/health/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(this::convertJwtToAuth)
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    JwtDecoder internalJwtDecoder(
            @Value("${jwt.secrets.internal}") String internalSecret,
            @Value("${security.internal.issuer}") String internalIssuer,
            @Value("${security.internal.expected-audience}") String internalAudience,
            @Value("#{'${security.internal.allowed-subjects}'.split(',')}") List<String> allowedSubjects
    ) {
        SecretKey key = new SecretKeySpec(internalSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

        OAuth2TokenValidator<Jwt> time = new JwtTimestampValidator(Duration.ofSeconds(30));

        OAuth2TokenValidator<Jwt> internalValidators = jwt -> {
            if (!internalIssuer.equals(jwt.getIssuer() != null ? jwt.getIssuer().toString() : null)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "wrong iss for internal", null));
            }
            if (jwt.getAudience() == null || !jwt.getAudience().contains(internalAudience)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "aud mismatch", null));
            }
            String sub = jwt.getSubject();
            if (sub == null || !allowedSubjects.contains(sub)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "subject not allowed", null));
            }
            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(time, internalValidators));
        return decoder;
    }

    @Bean
    JwtDecoder userJwtDecoder(
            @Value("${jwt.secrets.user}") String userSecret,
            @Value("${security.user.issuer}") String userIssuer
    ) {
        SecretKey key = new SecretKeySpec(userSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

        OAuth2TokenValidator<Jwt> time = new JwtTimestampValidator(Duration.ofSeconds(30));

        OAuth2TokenValidator<Jwt> userValidators = jwt -> {
            if (!userIssuer.equals(jwt.getIssuer() != null ? jwt.getIssuer().toString() : null)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "wrong iss for user", null));
            }

            try {
                UUID.fromString(jwt.getSubject());
            } catch (Exception e) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "sub must be UUID", null));
            }

            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(time, userValidators));
        return decoder;
    }

    @Bean
    JwtDecoder jwtDecoder(JwtDecoder internalJwtDecoder, JwtDecoder userJwtDecoder) {
        return token -> {
            try {
                return internalJwtDecoder.decode(token);
            } catch (JwtException e1) {
                try {
                    return userJwtDecoder.decode(token);
                } catch (JwtException e2) {
                    throw e2;
                }
            }
        };
    }

    // === Transform JWT -> Authentication with the required authorities ===
    private AbstractAuthenticationToken convertJwtToAuth(Jwt jwt) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final String iss = jwt.getClaimAsString("iss");

        if (internalIssuer.equals(iss)) {
            if ("game-service".equals(jwt.getSubject())) {
                authorities.add(new SimpleGrantedAuthority("SERVICE_GAME"));
            }

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        } else {
            //TODO use it when add roles in DB
            /*List<String> roles = Optional.ofNullable(jwt.getClaimAsStringList("roles"))
                    .orElseGet(() -> jwt.getClaimAsStringList("authorities"));

            if (roles != null) {
                for (String r : roles) {
                    authorities.add(new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r));
                }
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }*/

            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (jwt.getClaimAsBoolean("token_type") != null) {
                authorities.add(new SimpleGrantedAuthority("TYPE_" + jwt.getClaimAsString("token_type").toUpperCase()));
            }

            JwtUserPrincipal principal = new JwtUserPrincipal(
                    UUID.fromString(jwt.getSubject()),
                    jwt.getClaimAsString("nickname")
            );

            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
    }
}