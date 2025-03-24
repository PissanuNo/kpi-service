package com.kpi_service.kpi_service.core.configuration;


import com.kpi_service.kpi_service.core.security.AuthenFilter;
import com.kpi_service.kpi_service.core.security.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    public SecurityConfig (JwtAuthenticationProvider jwtAuthenticationProvider){
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Value("${spring.security.user.name}")
    private String secureUser;
    @Value("${spring.security.user.password}")
    private String securePassword;
    @Value("${spring.security.user.role}")
    private String secureRole;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(secureUser).password(securePassword).roles(secureRole);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/s/**", "/v*/s/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(IF_REQUIRED))
                .authenticationProvider(jwtAuthenticationProvider)
                .addFilterBefore(new AuthenFilter(), BasicAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/v1/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )
        ;
        return http.build();
    }

}
