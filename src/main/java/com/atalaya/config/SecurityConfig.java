package com.atalaya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/usuario/login",
                        "/usuario/registro",
                        "/usuario/guardar",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/producto/**"
                ).permitAll()

                .requestMatchers(
                        "/usuario/listado",
                        "/usuario/modificar/**",
                        "/usuario/actualizar",
                        "/usuario/eliminar/**",
                        "/categoria/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/carrito/**",
                        "/factura/**"
                ).hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
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
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}