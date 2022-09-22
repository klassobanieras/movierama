package com.tsompos.movierama.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

    public final static String USER_CLAIM = "username";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .httpBasic()
                .disable()
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors().and()
                .authorizeExchange(authorize -> authorize
                                           .pathMatchers(HttpMethod.GET, MOVIES_URL, MOVIES_URL + "/*")
                                           .permitAll()
//                        .and().
                );
//                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic()
//            .disable()
//            .formLogin(AbstractHttpConfigurer::disable)
//            .csrf(AbstractHttpConfigurer::disable)
//            .cors().and()
//            .authorizeRequests(authorize -> authorize
//                    .mvcMatchers(HttpMethod.GET, MOVIES_URL, MOVIES_URL + "/*")
//                    .permitAll()
//                .anyRequest()
//                    .authenticated()
//            )
//            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//    }
}
