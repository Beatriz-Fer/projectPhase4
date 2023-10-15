package com.beatriz.twittercloneproject.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // for authorization. inlcudes JXT token

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

   @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
       httpSecurity
               .csrf(csrf -> csrf.disable())
               .authorizeRequests(auth -> auth
                       .requestMatchers(HttpMethod.POST, "/api/user/check-email-unique")
                       .permitAll()
                       .requestMatchers(HttpMethod.POST, "/api/user/**")
                       .permitAll()
                       .requestMatchers("/api/user/**")
                       .permitAll()
                       .requestMatchers(HttpMethod.POST,"/api/**")
                       .permitAll()
                       .requestMatchers(HttpMethod.GET,"/api/**")
                       .permitAll()
                       .requestMatchers("/api/**")
                       .permitAll()
                       .anyRequest()
                       .authenticated()
               )
               .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .exceptionHandling(exceptions -> exceptions
                       .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                       .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
               );
       return httpSecurity.build();
   }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder(){return NimbusJwtDecoder.withPublicKey(this.publicKey).build();}

    @Bean
    JwtEncoder jwtEncoder(){
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

}

