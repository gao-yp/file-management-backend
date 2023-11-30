package com.gyp.jx.file.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class WebfluxSecurityConfig {
    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        PathPatternParserServerWebExchangeMatcher matcher1 = new PathPatternParserServerWebExchangeMatcher("/api/**");


        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityMatcher(new AndServerWebExchangeMatcher(matcher1))
                .authorizeExchange((exchanges) -> exchanges
                        .anyExchange().authenticated()
                )
                .httpBasic(withDefaults())
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)


        ;

        return http.build();
    }
}
