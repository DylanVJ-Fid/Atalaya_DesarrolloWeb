package com.atalaya.config;

import com.atalaya.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioService usuarioService) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/usuario/login",
                                "/usuario/registro",
                                "/usuario/guardar",
                                "/usuario/verificar",
                                "/registro/activacion/**",
                                "/registro/activar",
                                "/css/**",
                                "/js/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/usuario/**").hasRole("ADMIN")
                        .requestMatchers("/rol/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/carrito/**",
                                "/factura/**",
                                "/consultas/**"
                        ).authenticated()
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/usuario/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("correo")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/producto/", true)
                        .failureUrl("/usuario/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/usuario/login?logout=true")
                        .permitAll()
                )

                .userDetailsService(usuarioService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
