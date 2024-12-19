package raf.rs.nwp_project_kotlin.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import raf.rs.nwp_project_kotlin.filters.JwtFilter
import raf.rs.nwp_project_kotlin.service.UserService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // zbog @PreAuthorize anotacija
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http
            .cors { it.configurationSource { request ->
                val cors = CorsConfiguration()
                cors.allowedOrigins = listOf("http://localhost:4200")
                cors.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                cors.allowedHeaders = listOf("*")
                cors.allowCredentials = true
                cors
            }}
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS
                    .requestMatchers("/api/auth/**").permitAll() // dozvoli sve rute koje pocinju ovako da budu bez jwt (ovo ce biti samo login ruta)
                    .anyRequest().authenticated() // sve ostale rute trazi jwt
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // nece postojati sesija nego samo jwt
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationProvider(userService: UserService): AuthenticationProvider =
        DaoAuthenticationProvider().apply {
            setUserDetailsService(userService)
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}